package com.nosde.memo.domain.repository;

import com.nosde.memo.domain.model.RefreshToken;
import com.nosde.memo.domain.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUser(User user);
}
