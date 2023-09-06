package com.omar.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AlreadyConfirmedEmailException extends AuthenticationException {
    public AlreadyConfirmedEmailException(String emailAlreadyConfirmed) {
        super(emailAlreadyConfirmed);
    }
}
