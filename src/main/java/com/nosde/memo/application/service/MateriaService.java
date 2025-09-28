package com.nosde.memo.application.service;

import org.springframework.stereotype.Service;

import com.nosde.memo.application.dto.request.MateriaRequest;
import com.nosde.memo.domain.model.Materia;
import com.nosde.memo.domain.repository.MateriaRepository;
import com.nosde.memo.domain.repository.ProjetoRepository;

@Service
public class MateriaService {
    private final MateriaRepository materiaRepository;
    private final ProjetoRepository projetoRepository;
    public MateriaService(MateriaRepository materiaRepository, ProjetoRepository projetoRepository) {
        this.materiaRepository = materiaRepository;
        this.projetoRepository = projetoRepository;
    }

    public Materia criarMateria(Long projetoId, MateriaRequest materiaRequest) {
        Materia materia = new Materia();
        materia.setNome(materiaRequest.getNome());
        materia.setTotalTopicos(0);
        materia.setTopicosEstudados(0);
        materia.setQuestoesResolvidas(0);
        materia.setProjeto(projetoRepository.findById(projetoId).orElseThrow(() -> 
            new RuntimeException("Project not found with id: " + projetoId)));
        return materiaRepository.save(materia);
    }

}
