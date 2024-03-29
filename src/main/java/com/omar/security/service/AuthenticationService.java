package com.omar.security.service;

import com.omar.security.dtos.request.SignUpRequest;
import com.omar.security.dtos.request.LoginRequest;
import com.omar.security.dtos.response.LoginResponse;
import com.omar.security.dtos.response.RegisterResponse;
import com.omar.security.entities.User;
import com.omar.security.exceptions.AlreadyConfirmedEmailException;
import com.omar.security.exceptions.NotFoundAuthenticatedUserException;
import com.omar.security.exceptions.NotFoundTokenException;
import com.omar.security.exceptions.TokenExpiredException;

import java.util.concurrent.TimeoutException;

public interface AuthenticationService {
    RegisterResponse signup(SignUpRequest request) throws TimeoutException;

    LoginResponse login(LoginRequest request) throws NotFoundAuthenticatedUserException;


    String confirm(String token) throws NotFoundTokenException, AlreadyConfirmedEmailException, TokenExpiredException;

    User getUserFromToken(String token);

    String refreshToken(String refreshToken);
}
