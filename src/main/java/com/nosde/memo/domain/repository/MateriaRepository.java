package com.nosde.memo.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nosde.memo.domain.model.Materia;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    
}
