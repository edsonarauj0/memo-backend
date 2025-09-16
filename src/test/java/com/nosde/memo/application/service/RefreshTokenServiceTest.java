package com.nosde.memo.application.service;

import com.nosde.memo.domain.model.User;
import com.nosde.memo.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenServiceTest {

    private static RefreshTokenRepository mockRepository() {
        RefreshTokenRepository repository = Mockito.mock(RefreshTokenRepository.class);
        Mockito.when(repository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        return repository;
    }

    @Test
    void shouldAcceptDurationWithInlineComment() {
        RefreshTokenService service = new RefreshTokenService(
            mockRepository(),
            "P7D  // No trailing comment; valid ISO-8601 for 7 days"
        );

        Instant before = Instant.now();
        var token = service.createRefreshToken(new User());

        Duration duration = Duration.between(before, token.getExpiryDate());
        assertThat(duration).isGreaterThanOrEqualTo(Duration.ofDays(7));
        assertThat(duration).isLessThan(Duration.ofDays(7).plusSeconds(10));
    }

    @Test
    void shouldRejectInvalidDurationValues() {
        assertThatThrownBy(() -> new RefreshTokenService(mockRepository(), "invalid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid ISO-8601 duration");
    }
}
