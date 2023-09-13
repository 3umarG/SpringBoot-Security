package com.omar.security.dtos.response;


import com.omar.security.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String userName;
    private String email;
    private Role role;
    private boolean isAuthenticated;
    private String accessToken;
    private String refreshToken;
}
