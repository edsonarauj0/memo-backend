package com.nosde.memo.application.service;

import com.nosde.memo.domain.model.RefreshToken;
import com.nosde.memo.domain.model.User;
import com.nosde.memo.domain.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Duration refreshTokenDuration;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
        RefreshTokenRepository refreshTokenRepository,
        @Value("${jwt.refresh.expiration}") String refreshTokenDuration
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenDuration = parseRefreshTokenDuration(refreshTokenDuration);
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        String tokenValue = generateSecureTokenValue();
        token.setTokenHash(hashToken(tokenValue));
        token.setExpiryDate(Instant.now().plus(refreshTokenDuration));
        RefreshToken savedToken = refreshTokenRepository.save(token);
        savedToken.setPlainToken(tokenValue);
        return savedToken;
    }

    public Optional<RefreshToken> findByTokenValue(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return Optional.empty();
        }
        return refreshTokenRepository.findByTokenHash(hashToken(refreshTokenValue));
    }

    public RefreshToken rotateToken(RefreshToken existingToken) {
        refreshTokenRepository.delete(existingToken);
        return createRefreshToken(existingToken.getUser());
    }

    public boolean isValid(RefreshToken token) {
        return token.getExpiryDate().isAfter(Instant.now());
    }

    public void delete(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private String generateSecureTokenValue() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String tokenValue) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(tokenValue.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private Duration parseRefreshTokenDuration(String rawDuration) {
        if (rawDuration == null) {
            throw new IllegalArgumentException("Property 'jwt.refresh.expiration' must not be null");
        }

        String sanitized = stripInlineComments(rawDuration).trim();
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Property 'jwt.refresh.expiration' must not be empty");
        }

        try {
            return Duration.parse(sanitized);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                "Invalid ISO-8601 duration for property 'jwt.refresh.expiration': \"" + rawDuration + "\"",
                ex
            );
        }
    }

    private String stripInlineComments(String value) {
        int doubleSlashIndex = value.indexOf("//");
        int hashIndex = value.indexOf('#');

        int commentIndex = -1;
        if (doubleSlashIndex >= 0 && hashIndex >= 0) {
            commentIndex = Math.min(doubleSlashIndex, hashIndex);
        } else if (doubleSlashIndex >= 0) {
            commentIndex = doubleSlashIndex;
        } else if (hashIndex >= 0) {
            commentIndex = hashIndex;
        }

        return commentIndex >= 0 ? value.substring(0, commentIndex) : value;
    }
}
