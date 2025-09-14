package com.nosde.memo.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nosde.memo.domain.model.Projeto;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    List<Projeto> findByUsuarioId(Long usuarioId);
    Optional<Projeto> findByIdAndUsuarioId(Long projetoId, Long usuarioId);
}
