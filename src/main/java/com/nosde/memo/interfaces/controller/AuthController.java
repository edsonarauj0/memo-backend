package com.nosde.memo.interfaces.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nosde.memo.application.dto.AuthResponse;
import com.nosde.memo.application.dto.LoginRequest;
import com.nosde.memo.application.dto.LogoutRequest;
import com.nosde.memo.application.dto.RefreshTokenRequest;
import com.nosde.memo.application.dto.RegisterRequest;
import com.nosde.memo.application.dto.TokenResponse;
import com.nosde.memo.application.service.AuthService;
import com.nosde.memo.application.service.JwtService;
import com.nosde.memo.application.service.RefreshTokenService;
import com.nosde.memo.domain.model.User;
import com.nosde.memo.domain.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
        @RequestBody @Valid LoginRequest request,
        HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.login(request);
        setRefreshTokenCookie(response, authResponse.refreshToken());
        AuthResponse sanitizedResponse = new AuthResponse(
            authResponse.accessToken(),
            null,
            authResponse.user()
        );
        return ResponseEntity.ok(sanitizedResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @RequestBody(required = false) @Valid LogoutRequest request,
        @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshTokenCookie,
        Authentication authentication,
        HttpServletResponse response
    ) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String refreshTokenValue = resolveRefreshToken(refreshTokenCookie, request);
        if (refreshTokenValue == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        authService.logout(user, refreshTokenValue);
        SecurityContextHolder.clearContext();
        expireRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
        @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshTokenCookie,
        @RequestBody(required = false) @Valid RefreshTokenRequest request,
        HttpServletResponse response
    ) {
        String refreshTokenValue = resolveRefreshToken(refreshTokenCookie, request);
        if (refreshTokenValue == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        TokenResponse tokenResponse = authService.refresh(refreshTokenValue);
        setRefreshTokenCookie(response, tokenResponse.refreshToken());
        TokenResponse sanitizedResponse = new TokenResponse(tokenResponse.accessToken(), null);
        return ResponseEntity.ok(sanitizedResponse);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }

        String jwt = authHeader.substring(7);

        try {
            String userEmail = jwtService.extractUsername(jwt);
            var user = userRepository.findByEmail(userEmail).orElse(null);

            if (user == null) {
                return ResponseEntity.ok(false);
            }

            return ResponseEntity.ok(jwtService.isTokenValid(jwt, user));
        } catch (RuntimeException ex) {
            return ResponseEntity.ok(false);
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        Duration duration = refreshTokenService.getRefreshTokenDuration();
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(duration)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void expireRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(Duration.ZERO)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String resolveRefreshToken(String cookieValue, LogoutRequest request) {
        if (cookieValue != null && !cookieValue.isBlank()) {
            return cookieValue;
        }
        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            return request.refreshToken();
        }
        return null;
    }

    private String resolveRefreshToken(String cookieValue, RefreshTokenRequest request) {
        if (cookieValue != null && !cookieValue.isBlank()) {
            return cookieValue;
        }
        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            return request.refreshToken();
        }
        return null;
    }
}
