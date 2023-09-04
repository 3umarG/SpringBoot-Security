package com.omar.security.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolvers);

    Date extractExpiration(String token);
}
