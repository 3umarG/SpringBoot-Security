package com.omar.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class NotFoundTokenException extends AuthenticationException {
    public NotFoundTokenException(String s) {
        super(s);
    }
}
