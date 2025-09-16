package com.nosde.memo.application.service;

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
import com.nosde.memo.domain.exception.UserNotFoundException;
import com.nosde.memo.domain.model.RefreshToken;
import com.nosde.memo.domain.model.User;
import com.nosde.memo.domain.repository.UserRepository;
import com.nosde.memo.infrastructure.helper.ClassificacaoPerformance;
import com.nosde.memo.infrastructure.helper.UtHelper;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;  // Add this import if not present

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ProjetoService projetoService;

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
        user = userRepository.save(user);
        projetoService.criarProjetoPadrao(user);
        return new AuthResponse(null, null, new UserDto(user));
    }

    @Transactional  // Add this annotation to enable transaction for JPA operations
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
            refreshTokenService.deleteByUser(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            return new AuthResponse(accessToken, refreshToken.getPlainToken(), dto);

        } catch (IllegalArgumentException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("An error occurred during login process", e);
        }
    }

    public void logout(User user, String refreshTokenValue) {
        if (user == null) {
            throw new IllegalArgumentException("Usuário inválido");
        }

        if (UtHelper.isNullOrEmpty(refreshTokenValue)) {
            throw new IllegalArgumentException("Refresh token não pode ser vazio");
        }

        RefreshToken token = refreshTokenService.findByTokenValue(refreshTokenValue)
            .orElseThrow(() -> new BadCredentialsException("Refresh token inválido"));

        if (token.getUser() == null || !token.getUser().getId().equals(user.getId())) {
            throw new BadCredentialsException("Refresh token não pertence ao usuário autenticado");
        }

        refreshTokenService.delete(token);
    }

    public TokenResponse refresh(String refreshTokenValue) {
        RefreshToken token = refreshTokenService.findByTokenValue(refreshTokenValue)
            .orElseThrow(() -> new BadCredentialsException("Refresh token inválido ou expirado"));

        if (!refreshTokenService.isValid(token)) {
            refreshTokenService.delete(token);
            throw new BadCredentialsException("Refresh token inválido ou expirado");
        }

        RefreshToken rotatedToken = refreshTokenService.rotateToken(token);
        String newAccessToken = jwtService.generateToken(token.getUser());
        return new TokenResponse(newAccessToken, rotatedToken.getPlainToken());
    }

}
