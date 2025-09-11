package com.nosde.memo.application.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nosde.memo.application.dto.AuthResponse;
import com.nosde.memo.application.dto.LoginRequest;
import com.nosde.memo.application.dto.RegisterRequest;
import com.nosde.memo.domain.repository.UserRepository;
import com.nosde.memo.infrastructure.helper.ClassificacaoPerformance;

import lombok.RequiredArgsConstructor;
import com.nosde.memo.domain.model.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNome(request.nome());
        user.setSobrenome(request.sobrenome());
        user.setSexo(request.sexo());
        user.setCidade(request.cidade());
        user.setDiasEstudos(request.diasEstudos());
        user.setPrimeiroDiaSemana(request.primeiroDiaSemana());
        user.setPeriodoRevisao(request.periodoRevisao());
        user.setClassificacaoPerformance(new ClassificacaoPerformance());
        user.setFoto(request.foto());
        user.setRole("ROLE_USER");
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(), request.password()
            )
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

}
