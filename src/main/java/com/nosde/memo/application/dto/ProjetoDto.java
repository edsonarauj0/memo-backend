package com.nosde.memo.application.dto;

import com.nosde.memo.domain.model.Projeto;

import lombok.Data;

@Data
public class ProjetoDto {
    private Long id;
    private String nome;
    private String descricao;
    private String cargo;
    private String editais;
    private String imagemUrl;

    public ProjetoDto(Projeto projeto) {
        this.id = projeto.getId();
        this.nome = projeto.getNome();
        this.descricao = projeto.getDescricao();
        this.cargo = projeto.getCargo();
        this.editais = projeto.getEditais();
        this.imagemUrl = projeto.getImagemUrl();
    }
}
