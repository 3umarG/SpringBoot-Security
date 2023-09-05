package com.omar.security.service;

import com.omar.security.entities.ConfirmationToken;
import com.omar.security.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface ConfirmationTokenService {

    void save(ConfirmationToken confirmationToken);
}
