package com.omar.security.exceptions;

import com.omar.security.responses.ApiCustomResponse;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiCustomResponse<?>> handleUserNameNotFoundException(
            UsernameNotFoundException e,
            WebRequest request
    ) {
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
    public ResponseEntity<ApiCustomResponse<?>> handleUnAuthorizedException(
            AuthenticationException exception,
            WebRequest request
    ){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(ApiCustomResponse.builder()
                        .data(null)
                        .message(exception.getMessage())
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
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


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiCustomResponse<?>> globalException(Exception ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ApiCustomResponse.builder()
                    .data(null)
                    .message(ex.getMessage())
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
