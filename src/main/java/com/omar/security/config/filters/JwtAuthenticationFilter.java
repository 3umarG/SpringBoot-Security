package com.omar.security.config.filters;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.omar.security.service.JwtService;
import com.omar.security.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = checkForAuthorizationHeader(request, response, filterChain);
        if (authHeader == null) return;

        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUserName(jwt);

        if (isValidAuthenticationState(userEmail)) {
            UserDetails userDetails = userService.userDetailsService()
                    .loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                authenticateUserRequest(request, userDetails);
            }
        }
        filterChain.doFilter(request, response);
    }

    private static void authenticateUserRequest(HttpServletRequest request, UserDetails userDetails) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
    }

    private static boolean isValidAuthenticationState(String userEmail) {
        return StringUtils.isNotEmpty(userEmail)
               && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private static String checkForAuthorizationHeader(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authHeader)
            || !StringUtils.startsWith(authHeader, "Bearer ")) {

            filterChain.doFilter(request, response);
            return null;
        }
        return authHeader;
    }
}
