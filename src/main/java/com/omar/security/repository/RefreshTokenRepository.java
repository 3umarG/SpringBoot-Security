package com.omar.security.repository;

import com.omar.security.entities.RefreshToken;
import com.omar.security.entities.projection.RefreshTokenProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Ref;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByUser_Id(Integer id);

    @Query(value = "Select t.token , t.user_id as token_user_id ,u.user_id AS user_id " +
                   "FROM users_refresh_tokens as t " +
                   "JOIN users as u " +
                   "ON u.user_id = t.user_id " +
                   "WHERE u.email = ?1 " +
                   "AND Date(t.expires_on) > current_date " +
                   "AND t.revoked_on isnull ",
            nativeQuery = true)
    Optional<RefreshTokenProjection> findActiveTokenByUserEmailNativeQuery(String email);

    @Query("SELECT rt FROM RefreshToken rt " +
           "JOIN rt.user u " +
           "WHERE u.email = ?1 " +
           "AND rt.expiresOn > current_timestamp " +
           "AND rt.revokedOn IS NULL")
    Optional<RefreshToken> findActiveTokenWithUserEmailJPQL(String email);
}