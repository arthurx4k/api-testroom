package com.cefet.backend.service;

import com.cefet.backend.dto.AtividadeRequestDTO;
import com.cefet.backend.entity.Atividade;
import com.cefet.backend.entity.Professor;
import com.cefet.backend.entity.Questao;
import com.cefet.backend.entity.QuestaoAtividade;
import com.cefet.backend.exception.BusinessException;
import com.cefet.backend.exception.ResourceNotFoundException;
import com.cefet.backend.repository.AtividadeRepository;
import com.cefet.backend.repository.ProfessorRepository;
import com.cefet.backend.repository.QuestaoAtividadeRepository;
import com.cefet.backend.repository.QuestaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AtividadeService {

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private QuestaoRepository questaoRepository;

    @Autowired
    private QuestaoAtividadeRepository questaoAtividadeRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    public Atividade gerarAtividade(AtividadeRequestDTO dto, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));

        List<Questao> questoesDisponiveis = buscarQuestoesPorFiltros(dto);
        if (questoesDisponiveis.size() < dto.getQuantidade()) {
            throw new BusinessException("Não há questões suficientes. Disponíveis: "
                    + questoesDisponiveis.size() + ", solicitadas: " + dto.getQuantidade());
        }

        Collections.shuffle(questoesDisponiveis);
        List<Questao> selecionadas = questoesDisponiveis.stream()
                .limit(dto.getQuantidade())
                .collect(Collectors.toList());

        // Criar atividade
        Atividade atividade = new Atividade();
        atividade.setTitulo(dto.getTitulo());
        atividade.setDescricao(dto.getDescricao());
        atividade.setInstrucoes(dto.getInstrucoes());
        atividade.setProfessor(professor);
        atividade.setDataGeracao(LocalDateTime.now());

        BigDecimal total = selecionadas.stream()
                .map(Questao::getValorPontos)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        atividade.setValorPontos(total);

        atividade = atividadeRepository.save(atividade);

        List<QuestaoAtividade> lista = new ArrayList<>();
        int pos = 1;
        for (Questao q : selecionadas) {
            QuestaoAtividade qa = new QuestaoAtividade();
            qa.setAtividade(atividade);
            qa.setQuestao(q);
            qa.setPosicao(pos++);
            qa.setValorPontos(q.getValorPontos());
            lista.add(qa);
        }
        questaoAtividadeRepository.saveAll(lista);
        atividade.setQuestoes(lista);

        return atividade;
    }

    private List<Questao> buscarQuestoesPorFiltros(AtividadeRequestDTO dto) {
        if (dto.getCategoriaIds() != null && !dto.getCategoriaIds().isEmpty()) {
            return questaoRepository.findByCategoriasIdIn(dto.getCategoriaIds());
        }
        return questaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Atividade buscarPorId(Long id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada. Id: " + id));
    }
}