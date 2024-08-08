package com.tinqinacademy.authentication.persistance.repositories;

import com.tinqinacademy.authentication.persistance.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}