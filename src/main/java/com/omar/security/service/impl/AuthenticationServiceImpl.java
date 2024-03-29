package com.omar.security.service.impl;

import com.omar.security.dtos.response.LoginResponse;
import com.omar.security.dtos.response.RegisterResponse;
import com.omar.security.entities.ConfirmationToken;
import com.omar.security.entities.RefreshToken;
import com.omar.security.entities.projection.RefreshTokenProjection;
import com.omar.security.exceptions.AlreadyConfirmedEmailException;
import com.omar.security.exceptions.NotFoundAuthenticatedUserException;
import com.omar.security.exceptions.NotFoundTokenException;
import com.omar.security.exceptions.TokenExpiredException;
import com.omar.security.repository.RefreshTokenRepository;
import com.omar.security.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.omar.security.dtos.request.SignUpRequest;
import com.omar.security.dtos.request.LoginRequest;
import com.omar.security.entities.Role;
import com.omar.security.entities.User;
import com.omar.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSenderService emailSenderService;
    private final UserService userService;
    private final EmailBuilder emailBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Override
    @Transactional
    public RegisterResponse signup(SignUpRequest request) throws TimeoutException {

        // create user with given information
        User user = buildUserFromRequest(request);

        // save the user to db
        userRepository.save(user);

        // create confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = buildConfirmationToken(token, user);

        // save confirmation token
        confirmationTokenService.save(confirmationToken);
        LOGGER.info(confirmationToken.getToken());

        // send verification email
        sendConfirmationEmail(request, token);

        // make the response of the request
        return buildRegisterResponse(user);
    }

    private static RegisterResponse buildRegisterResponse(User user) {
        return RegisterResponse.builder()
                .userName(user.getEmail())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private void sendConfirmationEmail(SignUpRequest request, String token) throws TimeoutException {
        String confirmUri = "localhost:8080/api/v1/auth/confirm?token=" + token;
        emailSenderService.sendEmail(
                request.getEmail(),
                emailBuilder
                        .buildEmailBody(
                                request.getFirstName() + " " + request.getLastName(),
                                confirmUri
                        )
        );
    }

    private static ConfirmationToken buildConfirmationToken(String token, User user) {
        return new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5), // TODO : make that more long
                user);
    }

    private User buildUserFromRequest(SignUpRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request)
            throws NotFoundAuthenticatedUserException {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with that email!!"));

        Map<String, Object> claims = user.getClaims();
        var jwt = jwtService.generateToken(claims, user);

        authenticateLoginRequest(request);

        String token;

        Optional<RefreshTokenProjection> refreshTokenForUserEmail = refreshTokenRepository
                .findActiveTokenByUserEmailNativeQuery(user.getEmail());

        if (refreshTokenForUserEmail.isEmpty()) {
            RefreshToken refreshToken = createNewRefreshTokenForUser(user);
            saveRefreshTokenToDb(refreshToken);
            token = refreshToken.getToken();
        } else {
            token = refreshTokenForUserEmail.get().getToken();
        }

        return buildLoginResponse(user, jwt, token);
    }

    private void saveRefreshTokenToDb(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    private static RefreshToken createNewRefreshTokenForUser(User user) {
        RefreshToken refreshToken = RefreshToken.generateRefreshToken(user);
        return refreshToken;
    }

    private void authenticateLoginRequest(LoginRequest request) {
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
    }

    private static LoginResponse buildLoginResponse(User user, String jwt, String refreshToken) {
        return LoginResponse.builder()
                .isAuthenticated(true)
                .userName(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .build();
    }


    @Transactional
    @Override
    public String confirm(String token)
            throws NotFoundTokenException,
            AlreadyConfirmedEmailException,
            TokenExpiredException {

        ConfirmationToken confirmationToken = findConfirmationToken(token);

        validateConfirmationToken(confirmationToken);

        confirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(
                confirmationToken.getUser().getEmail());

        return "confirmed";
    }

    private static void validateConfirmationToken(ConfirmationToken confirmationToken) {
        // check for the token is already active
        if (confirmationToken.getConfirmedAt() != null) {
            LOGGER.warn("Already Confirmed Email!!");
            throw new AlreadyConfirmedEmailException("email already confirmed");
        }

        // check for expiration date
        LocalDateTime expirationDate = confirmationToken.getExpiresOn();
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired");
        }
    }

    private ConfirmationToken findConfirmationToken(String token) {
        return confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new NotFoundTokenException("Not Found Token!!"));
    }

    @Override
    public User getUserFromToken(String token) {
        var userName = jwtService.extractUserName(token);
        return userRepository.findByEmail(userName)
                .orElseThrow(() -> new NotFoundAuthenticatedUserException("There is no User with that Token!!"));
    }

    @Override
    public String refreshToken(String refreshToken) {
        RefreshTokenProjection refreshTokenProjection = getRefreshTokenProjection(refreshToken);

        LOGGER.info(String.valueOf(refreshTokenProjection.getTokenUserId()));
        User user = getUserById(refreshTokenProjection.getTokenUserId());

        String refreshedJwtToken = jwtService.generateToken(user);

        return refreshedJwtToken;
    }

    private User getUserById(Integer id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundAuthenticatedUserException("There is no user has this refresh token!!"));
        return user;
    }

    private RefreshTokenProjection getRefreshTokenProjection(String refreshToken) {
        RefreshTokenProjection refreshTokenProjection = refreshTokenRepository
                .findValidRefreshTokenWithToken(refreshToken)
                .orElseThrow(() -> new NotFoundTokenException("Not Found Token OR Revoked !!"));
        return refreshTokenProjection;
    }
}
