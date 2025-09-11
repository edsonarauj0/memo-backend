package com.nosde.memo.application.dto;

import java.time.DayOfWeek;
import java.util.Set;

import com.nosde.memo.domain.enums.SexoEnum;

public record RegisterRequest (String email, String password, String nome, String sobrenome, SexoEnum sexo, String cidade, Set<DayOfWeek> diasEstudos, DayOfWeek primeiroDiaSemana, Set<Integer> periodoRevisao, byte[] foto) {
}
