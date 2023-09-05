package com.omar.security.exceptions;


import org.springframework.security.core.AuthenticationException;

public class NotFoundAuthenticatedUserException extends AuthenticationException {
    public NotFoundAuthenticatedUserException(String msg) {
        super(msg);
    }
}
