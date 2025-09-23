package com.nosde.memo.application.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nosde.memo.domain.model.Projeto;

import lombok.Data;

@Data
public class ProjetoDetalhadoDto {
    private Long id;
    private String nome;
    private String cargo;
    private String descricao;
    private String organizacao;
    private UserDto usuario;
    private Integer codigo;
    private List<MateriaDto> materias = new ArrayList<>();

    public ProjetoDetalhadoDto(Projeto projeto) {
        this.id = projeto.getId();
        this.nome = projeto.getNome();
        this.cargo = projeto.getCargo();
        this.descricao = projeto.getDescricao();
        this.organizacao = projeto.getOrganizacao();
        this.usuario = new UserDto(projeto.getUsuario());
        this.codigo = projeto.getCodigo();
        this.materias = projeto.getMaterias().stream().map(MateriaDto::new).collect(Collectors.toList());
    }
}
