package com.nosde.memo.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nosde.memo.domain.model.Projeto;
import com.nosde.memo.domain.model.User;


public interface UserRepository extends JpaRepository<User, Long>  {
    Optional<User> findByEmail(String email);
    List<Projeto> findByProjetosId(Long projetoId);
}
