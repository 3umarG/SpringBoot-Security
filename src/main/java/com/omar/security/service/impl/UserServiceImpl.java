package com.omar.security.service.impl;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.omar.security.repository.UserRepository;
import com.omar.security.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserDetailsService userDetailsService() throws UsernameNotFoundException{
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("There is no User with that email!"));
    }
}
