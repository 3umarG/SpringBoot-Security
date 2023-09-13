package com.omar.security.exceptions;

import com.omar.security.responses.ApiCustomResponse;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;


@RestControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(AlreadyConfirmedEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleAlreadyConfirmedEmailException(
            AlreadyConfirmedEmailException e,
            WebRequest request
    ){
        LOGGER.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiCustomResponse.builder()
                        .statusCode(400)
                        .message(e.getMessage())
                        .isSuccess(false)
                        .data(null)
                        .build());
    }

    @ExceptionHandler(NotFoundTokenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleNotFoundTokenException(
            NotFoundTokenException e,
            WebRequest request
    ){
        LOGGER.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiCustomResponse.builder()
                        .statusCode(404)
                        .message(e.getMessage())
                        .isSuccess(false)
                        .data(null)
                        .build());
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleExpiredTokenException(
            TokenExpiredException e,
            WebRequest request
    ){
        LOGGER.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiCustomResponse.builder()
                        .statusCode(400)
                        .message(e.getMessage())
                        .isSuccess(false)
                        .data(null)
                        .build());
    }



    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ResponseEntity<ApiCustomResponse<?>> handleTimeoutException(
            TimeoutException e,
            WebRequest request
    ){
        LOGGER.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT.value())
                .body(ApiCustomResponse.builder()
                        .data(null)
                        .message(e.getMessage())
                        .statusCode(HttpStatus.REQUEST_TIMEOUT.value())
                        .isSuccess(false)
                        .build());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiCustomResponse<?>> handleUserNameNotFoundException(
            UsernameNotFoundException e,
            WebRequest request
    ) {
        LOGGER.warn(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiCustomResponse.builder()
                        .data(null)
                        .message(e.getMessage())
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .isSuccess(false)
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<ApiCustomResponse<?>> handleUnAuthorizedException(
            AuthenticationException exception,
            WebRequest request
    ){
        LOGGER.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(ApiCustomResponse.builder()
                        .data(null)
                        .message(exception.getMessage())
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .isSuccess(false)
                        .build());
    }


    @ExceptionHandler(NotFoundAuthenticatedUserException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiCustomResponse<?>> handleUnAuthenticatedException(
            NotFoundAuthenticatedUserException exception,
            WebRequest request
    ){
        LOGGER.warn(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                .body(ApiCustomResponse.builder()
                        .data(null)
                        .message(exception.getMessage())
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .isSuccess(false)
                        .build());
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiCustomResponse<?> jwtExceptionHandling(MalformedJwtException e, WebRequest request) {
        return ApiCustomResponse.builder()
                .data(null)
                .message(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .isSuccess(false)
                .build();
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiCustomResponse<?>> handleMissingRequestHeaderException(
            MissingRequestHeaderException e,
            WebRequest request
    ){
        return ResponseEntity.badRequest().body(
                ApiCustomResponse.builder()
                        .data(null)
                        .message(e.getMessage())
                        .statusCode(400)
                        .isSuccess(false)
                        .build()
        );
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiCustomResponse<?>> globalException(Exception ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ApiCustomResponse.builder()
                    .data(null)
                    .message(ex.getClass() +" - " + ex.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .isSuccess(false)
                    .build());

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> notValidRequestBody(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));

        Map<String, List<String>> result = new HashMap<>();
        result.put("errors", errors);

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
