package com.nosde.memo.interfaces.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nosde.memo.application.dto.AuthResponse;
import com.nosde.memo.application.dto.LoginRequest;
import com.nosde.memo.application.dto.RegisterRequest;
import com.nosde.memo.application.dto.UserDto;
import com.nosde.memo.application.service.AuthService;
import com.nosde.memo.application.service.JwtService;
import com.nosde.memo.application.service.RefreshTokenService;
import com.nosde.memo.domain.model.RefreshToken;
import com.nosde.memo.domain.repository.RefreshTokenRepository;
import com.nosde.memo.domain.repository.UserRepository;

import org.springframework.security.authentication.BadCredentialsException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new BadCredentialsException("Token inválido"));
        if (!refreshTokenService.isValid(token)) {
            throw new BadCredentialsException("Token expirado");
        }
        String newAccessToken = jwtService.generateToken(token.getUser());
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken, new UserDto(token.getUser())));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7); // remove "Bearer "
        String userEmail = jwtService.extractUsername(jwt);
        var user = userRepository.findByEmail(userEmail)
                    .orElse(null);
        return ResponseEntity.ok(jwtService.isTokenValid(jwt, user));
    }
}
