package com.omar.security.service.impl;

import com.omar.security.dao.response.LoginResponse;
import com.omar.security.dao.response.RegisterResponse;
import com.omar.security.entities.ConfirmationToken;
import com.omar.security.exceptions.NotFoundAuthenticatedUserException;
import com.omar.security.service.*;
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

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Override
    @Transactional
    public RegisterResponse signup(SignUpRequest request) throws TimeoutException {

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
                LocalDateTime.now().plusMinutes(5), // TODO : make that more long
                user);

        // save confirmation token
        confirmationTokenService.save(confirmationToken);
        LOGGER.info(confirmationToken.getToken());

        // TODO : SEND EMAIL WITH THAT TOKEN
        String confirmUri = "localhost:8080/api/v1/auth/confirm?token=" + token;
        emailSenderService.sendEmail(
                request.getEmail(),
                buildSimpleEmail(request.getFirstName() + " " + request.getLastName(), confirmUri));


        // make the response of the request
        return RegisterResponse.builder()
                .userName(user.getEmail())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
               "\n" +
               "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
               "\n" +
               "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
               "    <tbody><tr>\n" +
               "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
               "        \n" +
               "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
               "          <tbody><tr>\n" +
               "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
               "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
               "                  <tbody><tr>\n" +
               "                    <td style=\"padding-left:10px\">\n" +
               "                  \n" +
               "                    </td>\n" +
               "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
               "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
               "                    </td>\n" +
               "                  </tr>\n" +
               "                </tbody></table>\n" +
               "              </a>\n" +
               "            </td>\n" +
               "          </tr>\n" +
               "        </tbody></table>\n" +
               "        \n" +
               "      </td>\n" +
               "    </tr>\n" +
               "  </tbody></table>\n" +
               "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
               "    <tbody><tr>\n" +
               "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
               "      <td>\n" +
               "        \n" +
               "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
               "                  <tbody><tr>\n" +
               "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
               "                  </tr>\n" +
               "                </tbody></table>\n" +
               "        \n" +
               "      </td>\n" +
               "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
               "    </tr>\n" +
               "  </tbody></table>\n" +
               "\n" +
               "\n" +
               "\n" +
               "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
               "    <tbody><tr>\n" +
               "      <td height=\"30\"><br></td>\n" +
               "    </tr>\n" +
               "    <tr>\n" +
               "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
               "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
               "        \n" +
               "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\""+ link +"\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
               "        \n" +
               "      </td>\n" +
               "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
               "    </tr>\n" +
               "    <tr>\n" +
               "      <td height=\"30\"><br></td>\n" +
               "    </tr>\n" +
               "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
               "\n" +
               "</div></div>";
    }

    private String buildSimpleEmail(String name, String link) {
        return "<html>" +
               "<head>" +
               "<title>Email Confirmation</title>" +
               "</head>" +
               "<body>" +
               "<p>Hello, " + name + "!</p>" +
               "<p>Thank you for registering. Please click the button below to activate your account:</p>" +
               "<a href=\"" + link + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px;\">Activate Now</a>" +
               "<p>Link will expire in 15 minutes. See you soon!</p>" +
               "</body>" +
               "</html>";
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


    @Transactional
    @Override
    public String confirm(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(()-> new IllegalStateException("Not Found Token!!")); // TODO: custom not found token exception

        // check for the token is already active
        if(confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("email already confirmed"); // TODO : already confirmed exception
        }


        // check for expiration date
        LocalDateTime expirationDate = confirmationToken.getExpiresOn();
        if(expirationDate.isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Token expired"); // TODO : expired token exception
        }

        // update both the confiramtionToken for confirmedAt
        // & user himself make him enabled
        confirmationTokenService.setConfirmedAt(token);

        userService.enableAppUser(
                confirmationToken.getUser().getEmail());

        return "confirmed";
    }
}
