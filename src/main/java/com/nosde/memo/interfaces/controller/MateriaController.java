package com.nosde.memo.interfaces.controller;

import org.springframework.http.HttpStatus;
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
import com.nosde.memo.application.dto.request.MateriaRequest;
import com.nosde.memo.application.service.MateriaService;
import com.nosde.memo.application.service.ProjetoService;
import com.nosde.memo.domain.model.Materia;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class MateriaController {
    private final ProjetoService projetoService;
    private final MateriaService materiaService;

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

    @PostMapping("projeto/{projetoId}/materias")
    public ResponseEntity<?> criarMateria(
            @PathVariable Long projetoId,
            @RequestBody MateriaRequest materiaRequest) {
        Materia novaMateria = materiaService.criarMateria(projetoId, materiaRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaMateria);
    }
}
