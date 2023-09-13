package com.omar.security.entities;

import com.omar.security.service.JwtService;
import com.omar.security.service.impl.JwtServiceImpl;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users_refresh_tokens")
@Setter
@Getter
public class RefreshToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Integer id;

    private String token;

    private LocalDateTime createdOn;

    private LocalDateTime expiresOn;

    private LocalDateTime revokedOn;

    @Transient
    private boolean isExpired;

    @Transient
    private boolean isActive;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public RefreshToken(String token,
                        LocalDateTime createdOn,
                        LocalDateTime expiresOn) {
        this.token = token;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
    }

    public RefreshToken(Integer id,
                        String token,
                        LocalDateTime createdOn,
                        LocalDateTime expiresOn,
                        LocalDateTime revokedOn,
                        User user) {
        this.id = id;
        this.token = token;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
        this.revokedOn = revokedOn;
        this.isExpired = expiresOn.isBefore(LocalDateTime.now());
        this.isActive = !isExpired && revokedOn == null;
        this.user = user;
    }

    public static RefreshToken generateRefreshToken(User user) {
        return RefreshToken.builder()
                .createdOn(LocalDateTime.now())
                .expiresOn(LocalDateTime.now().plusDays(7))
                .token(JwtServiceImpl.generateRefreshToken())
                .user(user)
                .build();
    }
}
