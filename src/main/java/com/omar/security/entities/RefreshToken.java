package com.omar.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_refresh_tokens")
@Setter
@Getter
public class RefreshToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    private LocalDateTime createdOn;

    private LocalDateTime expiresOn;

    private LocalDateTime revokedOn;

    @Transient
    private boolean isExpired = expiresOn.isBefore(LocalDateTime.now());

    private boolean isActive = !isExpired && revokedOn == null;


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
}
