package com.omar.security.service;

import com.omar.security.dao.request.SignUpRequest;
import com.omar.security.dao.request.LoginRequest;
import com.omar.security.dao.response.LoginResponse;
import com.omar.security.dao.response.RegisterResponse;
import com.omar.security.exceptions.NotFoundAuthenticatedUserException;

import java.util.concurrent.TimeoutException;

public interface AuthenticationService {
    RegisterResponse signup(SignUpRequest request) throws TimeoutException;

    LoginResponse login(LoginRequest request) throws NotFoundAuthenticatedUserException;


    String confirm(String token);
}
