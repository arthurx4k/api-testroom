package com.cefet.backend.service;

import com.cefet.backend.dto.AtividadeComQuestoesRequestDTO;
import com.cefet.backend.dto.AtividadeRequestDTO;
import com.cefet.backend.dto.AtividadeResumoDTO;
import com.cefet.backend.entity.Alternativa;
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
import java.util.UUID;

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

        Atividade atividade = new Atividade();
        atividade.setTitulo(dto.getTitulo());
        atividade.setDescricao(dto.getDescricao());
        atividade.setInstrucoes(dto.getInstrucoes());
        atividade.setProfessor(professor);
        atividade.setDataGeracao(LocalDateTime.now());

        atividade = atividadeRepository.save(atividade);

        List<QuestaoAtividade> lista = new ArrayList<>();
        int pos = 1;
        for (Questao q : selecionadas) {
            QuestaoAtividade qa = new QuestaoAtividade();
            qa.setAtividade(atividade);
            qa.setQuestao(q);
            qa.setPosicao(pos++);
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

    @Transactional
    public List<Atividade> criarAtividadeComQuestoes(AtividadeComQuestoesRequestDTO dto, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));

        if (dto.getQuestoes() == null || dto.getQuestoes().isEmpty()) {
            throw new BusinessException("Selecione pelo menos uma questão.");
        }

        List<Long> questaoIds = dto.getQuestoes().stream()
                .map(AtividadeComQuestoesRequestDTO.QuestaoSelecionadaDTO::getQuestaoId)
                .collect(Collectors.toList());
        List<Questao> questoes = questaoRepository.findAllById(questaoIds);

        if (questoes.size() != questaoIds.size()) {
            throw new BusinessException("Alguma questão informada não existe.");
        }

        for (Questao q : questoes) {
            boolean acessivel = q.getProfessor().equals(professor) ||
                    q.getCategorias().stream().anyMatch(cat -> cat.getCompartilhadaCom().contains(professor));
            if (!acessivel) {
                throw new BusinessException("Questão " + q.getId() + " não pertence a você e não foi compartilhada.");
            }
        }

        String grupoId = UUID.randomUUID().toString();

        int qtdVersoes = dto.getQuantidadeVersoes() != null && dto.getQuantidadeVersoes() > 0
                ? dto.getQuantidadeVersoes()
                : 1;

        List<Atividade> versoes = new ArrayList<>();
        for (int v = 1; v <= qtdVersoes; v++) {
            Atividade atividade = new Atividade();
            atividade.setTitulo(qtdVersoes > 1
                    ? dto.getTitulo() + " (Versão " + v + ")"
                    : dto.getTitulo());
            atividade.setDescricao(dto.getDescricao());
            atividade.setInstrucoes(dto.getInstrucoes());
            atividade.setProfessor(professor);
            atividade.setDataGeracao(LocalDateTime.now());
            atividade.setQuantidadeVersoes(qtdVersoes);
            atividade.setGrupoId(grupoId);
            atividade.setValorPontos(BigDecimal.ZERO);
            atividade = atividadeRepository.save(atividade);

            List<AtividadeComQuestoesRequestDTO.QuestaoSelecionadaDTO> questoesEmbaralhadas = new ArrayList<>(
                    dto.getQuestoes());
            Collections.shuffle(questoesEmbaralhadas);

            List<QuestaoAtividade> lista = new ArrayList<>();
            int pos = 1;
            for (AtividadeComQuestoesRequestDTO.QuestaoSelecionadaDTO sel : questoesEmbaralhadas) {
                Questao q = questoes.stream()
                        .filter(qq -> qq.getId().equals(sel.getQuestaoId()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException("Questão não encontrada: " + sel.getQuestaoId()));

                QuestaoAtividade qa = new QuestaoAtividade();
                qa.setAtividade(atividade);
                qa.setQuestao(q);
                qa.setPosicao(pos++);
                qa.setValorPontos(sel.getValorPontos() != null
                        ? sel.getValorPontos()
                        : BigDecimal.ONE);

                List<Long> altIds = q.getAlternativas().stream()
                        .map(Alternativa::getId)
                        .collect(Collectors.toList());
                Collections.shuffle(altIds);
                qa.setOrdemAlternativas(altIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")));

                lista.add(qa);
            }
            questaoAtividadeRepository.saveAll(lista);
            atividade.setQuestoes(lista);

            BigDecimal total = lista.stream()
                    .map(QuestaoAtividade::getValorPontos)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            atividade.setValorPontos(total);
            atividade = atividadeRepository.save(atividade);

            versoes.add(atividade);

        }

        return versoes;
    }

    @Transactional(readOnly = true)
    public List<AtividadeResumoDTO> listarPorProfessor(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));
        return atividadeRepository.findByProfessorOrderByDataGeracaoDesc(professor).stream()
                .map(AtividadeResumoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void excluir(Long id) {
        if (!atividadeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Atividade não encontrada. Id: " + id);
        }
        atividadeRepository.deleteById(id);
    }
}