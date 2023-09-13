package com.omar.security.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "_confirmation_tokens")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresOn;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;


    public ConfirmationToken(String token,
                             LocalDateTime createdAt,
                             LocalDateTime expiresOn,
                             User user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresOn = expiresOn;
        this.user = user;
    }
}
