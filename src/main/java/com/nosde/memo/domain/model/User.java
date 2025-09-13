package com.nosde.memo.domain.model;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nosde.memo.domain.enums.EstadoEnum;
import com.nosde.memo.domain.enums.SexoEnum;
import com.nosde.memo.infrastructure.helper.ClassificacaoPerformance;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Senha é necessária")
    private String password;

    private String role;

    @Column(nullable = false)
    @NotBlank(message = "Nome é necessário")
    private String nome;

    @Column(nullable = false)
    @NotBlank(message = "Sobrenome é necessário")
    private String sobrenome;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "Sexo é necessário")
    private SexoEnum sexo;

    @Column(nullable = false)
    @NotBlank(message = "Cidade é necessária")
    private String cidade;

    @Column(nullable = false)
    @NotNull(message = "Estado é necessário")
    @Enumerated(EnumType.ORDINAL)
    private EstadoEnum estado;

    @Column(nullable = false)
    private byte[] foto;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_dias_estudos", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "dia_estudo")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> diasEstudos;

    @Enumerated(EnumType.STRING)
    private DayOfWeek primeiroDiaSemana;

    @Embedded
    private ClassificacaoPerformance classificacaoPerformance;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_periodo_revisao",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "periodo_revisao", nullable = false)
    private Set<Integer> periodoRevisao;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
