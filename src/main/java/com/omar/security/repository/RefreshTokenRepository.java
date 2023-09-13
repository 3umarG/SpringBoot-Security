package com.omar.security.repository;

import com.omar.security.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByUser_Id(Integer id);
    Optional<RefreshToken> findByUser_Email(String email);
}