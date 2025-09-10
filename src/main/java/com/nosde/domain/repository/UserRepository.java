package com.nosde.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nosde.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long>  {
    Optional<User> findByEmail(String email);
}
