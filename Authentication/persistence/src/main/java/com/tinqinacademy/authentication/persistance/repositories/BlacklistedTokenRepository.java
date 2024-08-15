package com.tinqinacademy.authentication.persistance.repositories;


import com.tinqinacademy.authentication.persistance.entities.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {
    List<BlacklistedToken> findAllByExpirationDateBefore(Date date);
}