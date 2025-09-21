package com.nosde.memo.application.service;

import org.springframework.stereotype.Service;

import com.nosde.memo.application.dto.ProjetoDto;
import com.nosde.memo.application.dto.ProjetoRequest;
import com.nosde.memo.domain.model.Projeto;
import com.nosde.memo.domain.model.User;
import com.nosde.memo.domain.repository.ProjetoRepository;
import com.nosde.memo.domain.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.nosde.memo.domain.exception.ResourceNotFoundException;

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

    public ProjetoDto criarProjeto(ProjetoRequest projetoRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); 

        User user = userRepository.findByEmail(username) 
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        Projeto projeto = new Projeto();
        projeto.setNome(projetoRequest.nome());
        projeto.setDescricao(projetoRequest.descricao());
        projeto.setCargo(projetoRequest.cargo());
        projeto.setOrganizacao(projetoRequest.organizacao());
        projeto.setUsuario(user);
        Projeto savedProjeto = projetoRepository.save(projeto);
        user.getProjetos().add(savedProjeto);
        userRepository.save(user);

        return new ProjetoDto(savedProjeto);
    }

    public User selecionarProjeto(Long projetoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        Projeto projeto = projetoRepository.findById(projetoId)
            .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado: " + projetoId));

        if (!projeto.getUsuario().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Projeto não pertence ao usuário autenticado: " + projetoId);
        }

        user.setProjetoSelecionadoId(projetoId);
        return userRepository.save(user);
    }
}
