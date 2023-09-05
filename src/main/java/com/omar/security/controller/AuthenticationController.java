package com.omar.security.controller;

import com.omar.security.dao.response.LoginResponse;
import com.omar.security.dao.response.RegisterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.omar.security.dao.request.SignUpRequest;
import com.omar.security.dao.request.LoginRequest;
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
    public ResponseEntity<?> confirm(@RequestParam String token){
        return ResponseEntity.ok(authenticationService.confirm(token));
    }
}
