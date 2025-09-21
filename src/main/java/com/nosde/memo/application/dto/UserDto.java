package com.nosde.memo.application.dto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.nosde.memo.domain.enums.EstadoEnum;
import com.nosde.memo.domain.enums.SexoEnum;
import com.nosde.memo.domain.model.User;
import com.nosde.memo.infrastructure.helper.ClassificacaoPerformance;

import lombok.Data;

@Data
public class UserDto {
  
    private String email;
    private String nome;
    private String sobrenome;
    private String sexo;
    private String cidade;
    private Set<DayOfWeek> diasEstudos;
    private DayOfWeek primeiroDiaSemana;
    private Set<Integer> periodoRevisao;
    private ClassificacaoPerformance classificacaoPerformance;
    private byte[] foto;
    private String role;
    private List<ProjetoDto> projetos;
    private Long projetoSelecionadoId;

    public UserDto(Long id, String email2, String nome2, String sobrenome2, SexoEnum sexo2, String cidade2,
            EstadoEnum estado, Set<DayOfWeek> diasEstudos2, DayOfWeek primeiroDiaSemana2, Set<Integer> periodoRevisao2,
            ClassificacaoPerformance classificacaoPerformance2, byte[] foto2, String role2) {
    }
    public UserDto() {
    }
    // Add constructor to map User entity to UserDto
    public UserDto(User user) {
        if (user == null) return;
        this.email = user.getEmail();
        this.nome = user.getNome();
        this.sobrenome = user.getSobrenome();
        this.sexo = user.getSexo() != null ? user.getSexo().name() : null;
        this.cidade = user.getCidade();
        this.role = user.getRole();
        this.diasEstudos = user.getDiasEstudos();
        this.primeiroDiaSemana = user.getPrimeiroDiaSemana();
        this.periodoRevisao = user.getPeriodoRevisao();
        this.classificacaoPerformance = user.getClassificacaoPerformance();
        this.foto = user.getFoto();
        this.projetoSelecionadoId = user.getProjetoSelecionadoId();
        this.projetos = user.getProjetos() != null ?
            user.getProjetos().stream()
                .map(projeto -> new ProjetoDto(projeto))
                .collect(Collectors.toList()) :
            null;
    }
}
