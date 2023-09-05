package com.omar.security.service;

import java.util.concurrent.TimeoutException;

public interface EmailSenderService {
    void sendEmail(String to,String email) throws TimeoutException;
}
