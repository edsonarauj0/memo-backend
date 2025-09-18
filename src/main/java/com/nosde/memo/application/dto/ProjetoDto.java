package com.nosde.memo.application.dto;

import com.nosde.memo.domain.model.Projeto;

import lombok.Data;

@Data
public class ProjetoDto {
    private Long id;
    private String nome;
    private String descricao;
    private String cargo;
    private String organizacao;
    
    public ProjetoDto(Projeto projeto) {
        this.id = projeto.getId();
        this.nome = projeto.getNome();
        this.descricao = projeto.getDescricao();
        this.cargo = projeto.getCargo();
    }
}
