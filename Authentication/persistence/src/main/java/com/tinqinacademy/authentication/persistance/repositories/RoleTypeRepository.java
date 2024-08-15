package com.tinqinacademy.authentication.persistance.repositories;

import com.tinqinacademy.authentication.persistance.entities.RoleType;
import com.tinqinacademy.authentication.persistance.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleTypeRepository extends JpaRepository<RoleType, UUID> {
    Optional<RoleType> findByRole( Role role);
}