package com.omar.security.controller;

import com.omar.security.dtos.response.LoginResponse;
import com.omar.security.dtos.response.RegisterResponse;
import com.omar.security.exceptions.AlreadyConfirmedEmailException;
import com.omar.security.exceptions.NotFoundTokenException;
import com.omar.security.exceptions.TokenExpiredException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.omar.security.dtos.request.SignUpRequest;
import com.omar.security.dtos.request.LoginRequest;
import com.omar.security.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> signup(@RequestBody SignUpRequest request) throws TimeoutException {
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam String token)
            throws NotFoundTokenException,
            AlreadyConfirmedEmailException,
            TokenExpiredException {
        return ResponseEntity.ok(authenticationService.confirm(token));
    }


    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken){
        if(refreshToken == null){
            throw new NotFoundTokenException("Required Refresh Token in the header");
        }

        return ResponseEntity.ok(authenticationService.refreshToken(refreshToken));
    }
}
