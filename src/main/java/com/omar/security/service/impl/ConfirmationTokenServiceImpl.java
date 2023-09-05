package com.omar.security.service.impl;

import com.omar.security.entities.ConfirmationToken;
import com.omar.security.repository.ConfirmationTokenRepository;
import com.omar.security.service.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {


    private final ConfirmationTokenRepository repository;

    @Override
    public void save(ConfirmationToken confirmationToken) {
        repository.save(confirmationToken);
    }
}
