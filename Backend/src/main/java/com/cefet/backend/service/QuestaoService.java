package com.cefet.backend.service;

import com.cefet.backend.dto.QuestaoRequestDTO;
import com.cefet.backend.dto.QuestaoResponseDTO;
import com.cefet.backend.entity.*;
import com.cefet.backend.exception.BusinessException;
import com.cefet.backend.exception.ResourceNotFoundException;
import com.cefet.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestaoService {

    @Autowired
    private QuestaoRepository questaoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AlternativaRepository alternativaRepository;

    @Transactional
    public QuestaoResponseDTO criar(QuestaoRequestDTO dto, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));

        if (dto.getCategoriaIds() == null || dto.getCategoriaIds().isEmpty()) {
            throw new BusinessException("É necessário selecionar pelo menos uma categoria.");
        }
        List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategoriaIds());
        for (Categoria cat : categorias) {
            if (!cat.getCriador().equals(professor) && !cat.getCompartilhadaCom().contains(professor)) {
                throw new BusinessException(
                        "Categoria " + cat.getId() + " não pertence a você e não foi compartilhada.");
            }
        }

        if (dto.getAlternativas() == null || dto.getAlternativas().size() < 2) {
            throw new BusinessException("É necessário cadastrar pelo menos duas alternativas.");
        }

        Questao questao = new Questao();
        questao.setProfessor(professor);
        questao.setTipoQuestao(dto.getTipoQuestao());
        questao.setEnunciado(dto.getEnunciado());
        questao.setValorPontos(dto.getValorPontos() != null ? dto.getValorPontos() : BigDecimal.ONE);
        questao.setCriadoPor(professor.getNome()); // automático

        questao = questaoRepository.save(questao);

        questao.setCategorias(new java.util.HashSet<>(categorias));

        for (QuestaoRequestDTO.AlternativaDTO altDto : dto.getAlternativas()) {
            Alternativa alt = new Alternativa();
            alt.setQuestao(questao);
            alt.setTexto(altDto.getTexto());
            alt.setVerdadeira(altDto.getVerdadeira());
            alternativaRepository.save(alt);
        }

        if (dto.getTipoQuestao() == TipoQuestao.UNICA_ESCOLHA) {
            long countTrue = dto.getAlternativas().stream().filter(QuestaoRequestDTO.AlternativaDTO::getVerdadeira)
                    .count();
            if (countTrue != 1) {
                throw new BusinessException(
                        "Para questão de única escolha, deve haver exatamente uma alternativa verdadeira.");
            }
        }

        return new QuestaoResponseDTO(questao);
    }

    @Transactional(readOnly = true)
    public List<QuestaoResponseDTO> listarPorProfessor(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));
        List<Questao> questoes = questaoRepository.findByProfessor(professor);
        return questoes.stream().map(QuestaoResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuestaoResponseDTO buscarPorId(Long id) {
        Questao questao = questaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada. Id: " + id));
        return new QuestaoResponseDTO(questao);
    }

    @Transactional
    public QuestaoResponseDTO atualizar(Long id, QuestaoRequestDTO dto, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));

        Questao questao = questaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada. Id: " + id));

        if (!questao.getProfessor().equals(professor)) {
            throw new BusinessException("Apenas o criador pode editar esta questão.");
        }

        if (dto.getCategoriaIds() == null || dto.getCategoriaIds().isEmpty()) {
            throw new BusinessException("É necessário selecionar pelo menos uma categoria.");
        }
        List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategoriaIds());
        for (Categoria cat : categorias) {
            if (!cat.getCriador().equals(professor) && !cat.getCompartilhadaCom().contains(professor)) {
                throw new BusinessException("Categoria " + cat.getId() + " não é acessível.");
            }
        }
        questao.setCategorias(new java.util.HashSet<>(categorias));

        questao.setEnunciado(dto.getEnunciado());
        questao.setValorPontos(dto.getValorPontos() != null ? dto.getValorPontos() : BigDecimal.ONE);

        if (dto.getTipoQuestao() != questao.getTipoQuestao()) {
            throw new BusinessException("Não é permitido alterar o tipo da questão.");
        }

        if (dto.getAlternativas() == null || dto.getAlternativas().size() < 2) {
            throw new BusinessException("É necessário cadastrar pelo menos duas alternativas.");
        }

        alternativaRepository.deleteByQuestao(questao);
        for (QuestaoRequestDTO.AlternativaDTO altDto : dto.getAlternativas()) {
            Alternativa alt = new Alternativa();
            alt.setQuestao(questao);
            alt.setTexto(altDto.getTexto());
            alt.setVerdadeira(altDto.getVerdadeira());
            alternativaRepository.save(alt);
        }

        if (dto.getTipoQuestao() == TipoQuestao.UNICA_ESCOLHA) {
            long countTrue = dto.getAlternativas().stream().filter(QuestaoRequestDTO.AlternativaDTO::getVerdadeira)
                    .count();
            if (countTrue != 1) {
                throw new BusinessException(
                        "Para questão de única escolha, deve haver exatamente uma alternativa verdadeira.");
            }
        }

        questao = questaoRepository.save(questao);
        return new QuestaoResponseDTO(questao);
    }

    @Transactional(readOnly = true)
    public List<QuestaoResponseDTO> listarPorCategoria(Long categoriaId, Long professorId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));
        if (!categoria.getCriador().equals(professor) && !categoria.getCompartilhadaCom().contains(professor)) {
            throw new BusinessException("Você não tem acesso a esta categoria.");
        }
        List<Questao> questoes = questaoRepository.findByCategorias_Id(categoriaId);
        return questoes.stream().map(QuestaoResponseDTO::new).toList();
    }

    @Transactional
    public void excluir(Long id, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));

        Questao questao = questaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada. Id: " + id));

        if (!questao.getProfessor().equals(professor)) {
            throw new BusinessException("Apenas o criador pode excluir esta questão.");
        }

        alternativaRepository.deleteByQuestao(questao);
        questaoRepository.delete(questao);
    }
}