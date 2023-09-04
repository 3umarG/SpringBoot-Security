package com.omar.security.service;

import com.omar.security.dao.request.SignUpRequest;
import com.omar.security.dao.request.LoginRequest;
import com.omar.security.dao.response.LoginResponse;
import com.omar.security.dao.response.RegisterResponse;

public interface AuthenticationService {
    RegisterResponse signup(SignUpRequest request);

    LoginResponse login(LoginRequest request);
}
