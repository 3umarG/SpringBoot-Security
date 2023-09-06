package com.omar.security.exceptions.entrypoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.omar.security.exceptions.AlreadyConfirmedEmailException;
import com.omar.security.exceptions.NotFoundTokenException;
import com.omar.security.exceptions.TokenExpiredException;
import com.omar.security.responses.ApiCustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component("delegatedAuthenticationEntryPoint")
public class DelegatedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private ObjectMapper mapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(DelegatedAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {
        // TODO : that will give the handling process to the @ControllerAdvice
        LOGGER.warn("Enter the `Commence` method");
        resolver.resolveException(request, response, null, authException);

        // TODO : handle the exception from entry point
        /*
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        response.setContentType("application/json");

        LOGGER.warn(authException.getMessage());

        if (authException instanceof AlreadyConfirmedEmailException) {
            var apiResponse = ApiCustomResponse.builder()
                    .message("Email already confirmed")
                    .statusCode(400)
                    .isSuccess(false).build();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(mapper.writeValueAsString(apiResponse));
        } else if (authException instanceof TokenExpiredException) {
            var apiResponse = ApiCustomResponse.builder()
                    .message(authException.getMessage())
                    .statusCode(401)
                    .isSuccess(false).build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(mapper.writeValueAsString(apiResponse));
        } else if (authException instanceof NotFoundTokenException) {
            var apiResponse = ApiCustomResponse.builder()
                    .message(authException.getMessage())
                    .statusCode(404)
                    .isSuccess(false).build();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
        } else {
            var apiResponse = ApiCustomResponse.builder()
                    .message("Authentication Failed")
                    .statusCode(401)
                    .isSuccess(false).build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(mapper.writeValueAsString(apiResponse));
        }

         */
    }
}
