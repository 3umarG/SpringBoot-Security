package com.omar.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class TokenExpiredException extends AuthenticationException {
    public TokenExpiredException(String tokenExpired) {
        super(tokenExpired);
    }
}
