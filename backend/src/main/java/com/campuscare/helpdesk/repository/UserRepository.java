package com.campuscare.helpdesk.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campuscare.helpdesk.entity.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
