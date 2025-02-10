package com.thachnn.ShopIoT.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.thachnn.ShopIoT.dto.request.IntrospectRequest;
import com.thachnn.ShopIoT.dto.request.LoginRequest;
import com.thachnn.ShopIoT.dto.request.LogoutRequest;
import com.thachnn.ShopIoT.dto.request.RefreshRequest;
import com.thachnn.ShopIoT.dto.response.AuthenticationResponse;
import com.thachnn.ShopIoT.dto.response.IntrospectResponse;
import com.thachnn.ShopIoT.model.User;

import java.text.ParseException;

public interface IAuthenticationService {

    public AuthenticationResponse authenticate(LoginRequest request);

    public String generateToken(User user);

    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException;

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;

    public void logout(LogoutRequest request) throws ParseException, JOSEException;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;

    public AuthenticationResponse oauth2GoogleAuthenticate(String code);
}
