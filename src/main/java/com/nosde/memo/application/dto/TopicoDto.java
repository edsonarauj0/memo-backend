package com.nosde.memo.application.dto;

import com.nosde.memo.domain.model.Topico;

import lombok.Data;

@Data
public class TopicoDto {
    private Long id;
    private String titulo;
    private boolean estudado;
    private boolean revisado;
    private int questoesResolvidas;
    private MateriaDto materia;

    public TopicoDto(Topico topico) {
        this.id = topico.getId();
        this.titulo = topico.getTitulo();
        this.estudado = topico.isEstudado();
        this.revisado = topico.isRevisado();
        this.questoesResolvidas = topico.getQuestoesResolvidas();
        this.materia = new MateriaDto(topico.getMateria());
    }
}
