package com.nosde.memo.interfaces.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nosde.memo.application.dto.ProjetoDetalhadoDto;
import com.nosde.memo.application.dto.ProjetoDto;
import com.nosde.memo.application.dto.ProjetoRequest;
import com.nosde.memo.application.dto.ProjetoSelecionadoRequest;
import com.nosde.memo.application.dto.UserDto;
import com.nosde.memo.application.service.ProjetoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projeto")
@RequiredArgsConstructor
public class ProjetoController {
    private final ProjetoService projetoService;


    @PostMapping
    public ResponseEntity<ProjetoDto> createProjeto(@RequestBody ProjetoRequest projeto) {
        return ResponseEntity.ok(projetoService.criarProjeto(projeto));
    }

    @PostMapping("/selecionar")
    public ResponseEntity<UserDto> selecionarProjeto(@RequestBody ProjetoSelecionadoRequest request) {
        return ResponseEntity.ok(new UserDto(projetoService.selecionarProjeto(request.getProjetoId())));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjetoDetalhadoDto> getProjeto(@PathVariable Long id) {
        return ResponseEntity.ok(projetoService.buscarProjetoPorId(id));
    }

}
