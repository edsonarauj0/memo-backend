package com.nosde.memo.application.service;

import org.springframework.stereotype.Service;

import com.nosde.memo.domain.model.Projeto;
import com.nosde.memo.domain.model.User;
import com.nosde.memo.domain.repository.ProjetoRepository;
import com.nosde.memo.domain.repository.UserRepository;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;
    private final UserRepository userRepository;

    public ProjetoService(ProjetoRepository projetoRepository, UserRepository userRepository) {
        this.projetoRepository = projetoRepository;
        this.userRepository = userRepository;
    }

    public Projeto criarProjetoPadrao(User user) {
        Projeto projeto = new Projeto();
        projeto.setNome("Projeto Padrão");
        projeto.setDescricao("Projeto Padrão");
        projeto.setUsuario(user);
        Projeto savedProjeto = projetoRepository.save(projeto);
        user.getProjetos().add(savedProjeto);
        userRepository.save(user);
        return savedProjeto;
    }
}
