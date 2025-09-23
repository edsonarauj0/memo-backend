package com.nosde.memo.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nosde.memo.domain.model.Projeto;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    List<Projeto> findByUsuarioId(Long usuarioId);
    Optional<Projeto> findByIdAndUsuarioId(Long projetoId, Long usuarioId);
    @Query("SELECT COALESCE(MAX(p.codigo), 0) FROM Projeto p WHERE p.usuario.id = :usuarioId")
    int findMaxCodigoByUsuario(@Param("usuarioId") Long usuarioId);
}
