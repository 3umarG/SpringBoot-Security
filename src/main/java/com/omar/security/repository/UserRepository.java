package com.omar.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.omar.security.entities.User;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Since email is unique, we'll find users by email
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
           "SET a.enabled = TRUE " +
           "WHERE a.email = ?1")
    int enableAppUser(String email);
}
