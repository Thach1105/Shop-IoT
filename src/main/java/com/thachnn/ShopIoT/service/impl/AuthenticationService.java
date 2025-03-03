package com.thachnn.ShopIoT.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.thachnn.ShopIoT.dto.request.*;
import com.thachnn.ShopIoT.dto.response.AuthenticationResponse;
import com.thachnn.ShopIoT.dto.response.IntrospectResponse;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.model.InvalidatedToken;
import com.thachnn.ShopIoT.model.RedisToken;
import com.thachnn.ShopIoT.model.User;
import com.thachnn.ShopIoT.repository.InvalidatedTokenRepository;
import com.thachnn.ShopIoT.repository.RedisTokenRepository;
import com.thachnn.ShopIoT.repository.httpclient.OutboundIdentityClient;
import com.thachnn.ShopIoT.repository.httpclient.OutboundUserClient;
import com.thachnn.ShopIoT.service.IAuthenticationService;
import com.thachnn.ShopIoT.util.TransactionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j(topic = "AUTHENTICATION-SERVICE")
@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Value("${outbound.google.client-id}")
    protected String GOOGLE_CLIENT_ID;

    @Value("${outbound.google.client-secret}")
    protected String GOOGLE_CLIENT_SECRET;

    @Value("${outbound.google.redirect-uri}")
    protected String REDIRECT_URI;

    protected final String GRANT_TYPE = "authorization_code";

    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final OutboundIdentityClient outboundIdentityClient;
    private final OutboundUserClient outboundUserClient;
    private final EmailService emailService;
    private final RedisTokenService redisTokenService;

    // Login
    @Override
    public AuthenticationResponse authenticate(LoginRequest request) {
        User user = userService.getByUsername(request.getUsername());
        if (user != null) {
            boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if (!authenticated) throw new AppException(ErrorApp.PASSWORD_INCORRECT);

            String token = generateToken(user);
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        }
        throw new AppException(ErrorApp.USER_NOTFOUND);
    }

    // Generate Token
    @Override
    public String generateToken(User user) {
        // Build user data in token
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("id", user.getId());
        dataUser.put("username", user.getUsername());
        dataUser.put("email", user.getEmail());
        dataUser.put("fullName", user.getFullName());

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("Shop IoT")
                .issueTime(new Date(System.currentTimeMillis()))
                .expirationTime(new Date(System.currentTimeMillis() + VALID_DURATION * 1000))
                .jwtID(UUID.randomUUID().toString())
                .claim("data", dataUser)
                .claim("scope", user.getRole().getName())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            log.info("Generate token for user: {}", user);
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot create token: ", e);
        }
    }

    // Verify Token
    @Override
    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = isRefresh
                ? Date.from(Instant.now().plusSeconds(REFRESHABLE_DURATION))
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorApp.UNAUTHENTICATION);
        }

        /*if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            System.out.println(signedJWT.getJWTClaimsSet().getJWTID());
            throw new AppException(ErrorApp.UNAUTHENTICATION);
        }*/
        if (redisTokenService.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            System.out.println(signedJWT.getJWTClaimsSet().getJWTID());
            throw new AppException(ErrorApp.UNAUTHENTICATION);
        }

        return signedJWT;
    }

    // Refresh Token
    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signJWT = verifyToken(request.getToken(), true);

        String jit = signJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = new Date(signJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        /*InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);*/
        redisTokenService.save(
                RedisToken.builder()
                        .id(jit)
                        .expiryTime(expiryTime)
                        .accessToken(request.getToken())
                .build()
        );

        // Generate new token
        String username = signJWT.getJWTClaimsSet().getSubject();
        User user = userService.getByUsername(username);

        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    // Logout
    @Override
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            SignedJWT signToken = verifyToken(request.getToken(), true);

            String jwtID = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = new Date(signToken.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());

            /*System.out.println(jwtID);
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .expiryTime(expiryTime)
                    .id(jwtID)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);*/

            redisTokenService.save(
                    RedisToken.builder()
                            .id(jwtID)
                            .expiryTime(expiryTime)
                            .accessToken(request.getToken())
                            .build()
            );
        } catch (AppException e) {
            log.info("Token already expired");
        }
    }

    // Introspect
    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();
        boolean valid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            valid = false;
        }

        return IntrospectResponse.builder()
                .valid(valid)
                .build();
    }

    // OAuth2 Google Authenticate
    @Override
    public AuthenticationResponse oauth2GoogleAuthenticate(String code) {
        var response = outboundIdentityClient.exchangeToken(
                ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(GOOGLE_CLIENT_ID)
                        .clientSecret(GOOGLE_CLIENT_SECRET)
                        .redirectUri(REDIRECT_URI)
                        .grantType(GRANT_TYPE)
                        .build());

        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        User user = new User();
        if (userService.existingEmail(userInfo.getEmail())) {
            user = userService.getByEmail(userInfo.getEmail());
        } else {
            String password = TransactionUtil.getRandomNumber(10);
            user = userService.create(CreateUserRequest.builder()
                    .username(userInfo.getEmail())
                    .password(password)
                    .fullName(userInfo.getName())
                    .email(userInfo.getEmail())
                    .build());

            String subject = "Chào mừng bạn đến với Shop IoT";
            String body = "Cảm ơn bạn đã đăng ký!\n"
                    + "Thông tin tài khoản của bạn:\n"
                    + "Tên tài khoản: " + userInfo.getEmail() + "\n"
                    + "Mật khẩu: " + password;

            emailService.sendSimpleMessage(userInfo.getEmail(), subject, body);
        }
        
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
