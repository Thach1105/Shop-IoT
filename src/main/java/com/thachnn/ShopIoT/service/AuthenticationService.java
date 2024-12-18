package com.thachnn.ShopIoT.service;

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
import com.thachnn.ShopIoT.model.User;
import com.thachnn.ShopIoT.repository.InvalidatedTokenRepository;
import com.thachnn.ShopIoT.repository.httpclient.OutboundIdentityClient;
import com.thachnn.ShopIoT.repository.httpclient.OutboundUserClient;
import com.thachnn.ShopIoT.util.TransactionUtil;
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

@Slf4j
@Service
public class AuthenticationService {

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

    public AuthenticationService(
            InvalidatedTokenRepository invalidatedTokenRepository,
            PasswordEncoder passwordEncoder,
            UserService userService,
            OutboundIdentityClient outboundIdentityClient,
            OutboundUserClient outboundUserClient,
            EmailService emailService
    ) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.outboundIdentityClient = outboundIdentityClient;
        this.outboundUserClient = outboundUserClient;
        this.emailService = emailService;
    }

    // Login
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
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("data", dataUser)
                .claim("scope", user.getRole().getName())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot create token: ", e);
        }
    }

    // Verify Token
    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = isRefresh
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorApp.UNAUTHENTICATION);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorApp.UNAUTHENTICATION);
        }

        return signedJWT;
    }

    // Refresh Token
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signJWT = verifyToken(request.getToken(), true);

        String jit = signJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = new Date(signJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        // Generate new token
        String username = signJWT.getJWTClaimsSet().getSubject();
        User user = userService.getByUsername(username);

        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    // Logout
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            SignedJWT signToken = verifyToken(request.getToken(), true);

            String jwtID = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = new Date(signToken.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli());

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .expiryTime(expiryTime)
                    .id(jwtID)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token already expired");
        }
    }

    // Introspect
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
