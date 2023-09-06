package com.omar.security.controller;

import com.omar.security.entities.User;
import com.omar.security.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/resource")
@RequiredArgsConstructor
public class AuthorizationController {
    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<User> sayHello(
            @RequestHeader("Authorization") String token
    ) {
        var jwtToken = token.substring(7);
        return ResponseEntity.ok(authenticationService.getUserFromToken(jwtToken));
    }
}
