package com.omar.security.service.impl;

import com.omar.security.dao.response.LoginResponse;
import com.omar.security.dao.response.RegisterResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.omar.security.dao.request.SignUpRequest;
import com.omar.security.dao.request.LoginRequest;
import com.omar.security.entities.Role;
import com.omar.security.entities.User;
import com.omar.security.repository.UserRepository;
import com.omar.security.service.AuthenticationService;
import com.omar.security.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse signup(SignUpRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .userName(user.getEmail())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return LoginResponse.builder()
                .isAuthenticated(true)
                .userName(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(jwt)
                .build();
    }
}
