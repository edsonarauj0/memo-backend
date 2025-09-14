package com.nosde.memo.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nosde.memo.application.dto.AuthResponse;
import com.nosde.memo.application.dto.LoginRequest;
import com.nosde.memo.application.dto.RegisterRequest;
import com.nosde.memo.application.dto.TokenResponse;
import com.nosde.memo.application.dto.UserDto;
import com.nosde.memo.domain.repository.RefreshTokenRepository;
import com.nosde.memo.domain.repository.UserRepository;
import com.nosde.memo.infrastructure.helper.ClassificacaoPerformance;
import com.nosde.memo.infrastructure.helper.UtHelper;

import lombok.RequiredArgsConstructor;

import com.nosde.memo.domain.exception.UserNotFoundException;
import com.nosde.memo.domain.model.RefreshToken;
import com.nosde.memo.domain.model.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse register(RegisterRequest request) {
        var user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNome(request.nome());
        user.setSobrenome(request.sobrenome());
        user.setSexo(request.sexo());
        user.setCidade(request.cidade());
        user.setEstado(request.estado());
        user.setDiasEstudos(request.diasEstudos());
        user.setPrimeiroDiaSemana(request.primeiroDiaSemana());
        user.setPeriodoRevisao(request.periodoRevisao());
        user.setClassificacaoPerformance(new ClassificacaoPerformance());
        user.setFoto(request.foto());
        user.setRole("ROLE_USER");
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(null, null, new UserDto());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            if (UtHelper.isNullOrEmpty(request.email()) || !UtHelper.isValidEmail(request.email())) {
                throw new IllegalArgumentException("Invalid email format");
            }

            if (UtHelper.isNullOrEmpty(request.password())) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("Senha ou email incorretos"));

            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.email(), 
                        request.password()
                    )
                );
            } catch (Exception e) {
                throw new UserNotFoundException("Senha ou email incorretos");
            }

            var dto = new UserDto(user);

            String accessToken = jwtService.generateToken(user);
            String refreshToken = UUID.randomUUID().toString();
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setExpiryDate(java.time.Instant.now().plusSeconds(86400));
            refreshTokenRepository.save(refreshTokenEntity);
            return new AuthResponse(accessToken, refreshToken, dto);

        } catch (IllegalArgumentException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during login process", e);
        }
    }

    public TokenResponse refresh(String refreshToken) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (!token.isPresent() || token.get().getExpiryDate().isBefore(java.time.Instant.now())) {
            throw new BadCredentialsException("Refresh token inválido ou expirado");
        }
        String newAccessToken = jwtService.generateToken(token.get().getUser());
        return new TokenResponse(newAccessToken, refreshToken);
    }

}
