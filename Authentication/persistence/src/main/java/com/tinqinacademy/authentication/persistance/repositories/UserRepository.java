package com.tinqinacademy.authentication.persistance.repositories;

import com.tinqinacademy.authentication.persistance.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername( String username);

    Optional<User> findByConfirmationCode(String confirmationCode);

}