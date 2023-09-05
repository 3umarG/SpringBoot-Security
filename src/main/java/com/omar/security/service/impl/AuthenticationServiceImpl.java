package com.omar.security.service.impl;

import com.omar.security.dao.response.LoginResponse;
import com.omar.security.dao.response.RegisterResponse;
import com.omar.security.entities.ConfirmationToken;
import com.omar.security.exceptions.NotFoundAuthenticatedUserException;
import com.omar.security.service.ConfirmationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;

    private Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Override
    public RegisterResponse signup(SignUpRequest request) {

        // create user with given information
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        // save the user to db
        userRepository.save(user);

        // create confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1), // TODO : make that more long
                user);

        // save confirmation token
        confirmationTokenService.save(confirmationToken);
        logger.info(confirmationToken.getToken());

        // TODO : SEND EMAIL WITH THAT TOKEN


        // make the response of the request
        return RegisterResponse.builder()
                .userName(user.getEmail())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) throws NotFoundAuthenticatedUserException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new NotFoundAuthenticatedUserException("Not Authenticated User");
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with that email!!"));
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
