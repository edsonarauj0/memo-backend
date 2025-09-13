package com.nosde.memo.application.dto;

import java.time.DayOfWeek;
import java.util.Set;

import com.nosde.memo.domain.enums.EstadoEnum;
import com.nosde.memo.domain.enums.SexoEnum;
import com.nosde.memo.infrastructure.helper.ClassificacaoPerformance;

import lombok.Data;

@Data
public class UserDto {
    public UserDto(Long id, String email2, String nome2, String sobrenome2, SexoEnum sexo2, String cidade2,
            EstadoEnum estado, Set<DayOfWeek> diasEstudos2, DayOfWeek primeiroDiaSemana2, Set<Integer> periodoRevisao2,
            ClassificacaoPerformance classificacaoPerformance2, byte[] foto2, String role2) {
    }
    public UserDto() {
    }
    private String email;
    private String nome;
    private String sobrenome;
    private String sexo;
    private String cidade;
    private int diasEstudos;
    private int primeiroDiaSemana;
    private int periodoRevisao;
    private String classificacaoPerformance;
    private String foto;
    private String role;
}
