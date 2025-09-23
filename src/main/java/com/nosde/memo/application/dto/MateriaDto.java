package com.nosde.memo.application.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nosde.memo.domain.model.Materia;

import lombok.Data;

@Data
public class MateriaDto {
    private Long id;
    private String nome;
    private int totalTopicos;
    private int topicosEstudados; 
    private int questoesResolvidas;
    private List<TopicoDto> topicos = new ArrayList<>();

    public MateriaDto(Materia materia) {
        this.id = materia.getId();
        this.nome = materia.getNome();
        this.totalTopicos = materia.getTotalTopicos();
        this.topicosEstudados = materia.getTopicosEstudados();
        this.questoesResolvidas = materia.getQuestoesResolvidas();
        this.topicos = materia.getTopicos().stream()
            .map(topico -> new TopicoDto(topico))
            .collect(Collectors.toList());
    }
}
