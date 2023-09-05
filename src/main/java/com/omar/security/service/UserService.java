package com.omar.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    UserDetailsService userDetailsService() throws UsernameNotFoundException;

    int enableAppUser(String email);
}
