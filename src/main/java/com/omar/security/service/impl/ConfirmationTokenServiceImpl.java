package com.omar.security.service.impl;

import com.omar.security.entities.ConfirmationToken;
import com.omar.security.repository.ConfirmationTokenRepository;
import com.omar.security.service.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {


    private final ConfirmationTokenRepository repository;

    @Override
    public void save(ConfirmationToken confirmationToken) {
        repository.save(confirmationToken);
    }

    @Override
    public Optional<ConfirmationToken> getToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public int setConfirmedAt(String token) {
        return repository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
