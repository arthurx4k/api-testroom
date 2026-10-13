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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestaoService {

    @Autowired private QuestaoRepository questaoRepository;
    @Autowired private QuestaoAtividadeRepository questaoAtividadeRepository;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private AlternativaRepository alternativaRepository;

    private boolean temAcessoCategoria(Professor professor, Categoria cat) {
        if (cat.getCriador().getId().equals(professor.getId())) return true;
        return cat.getCompartilhadaCom().stream()
                .anyMatch(p -> p.getId().equals(professor.getId()));
    }

    @Transactional
    public QuestaoResponseDTO criar(QuestaoRequestDTO dto, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));

        if (dto.getCategoriaIds() == null || dto.getCategoriaIds().isEmpty()) {
            throw new BusinessException("É necessário selecionar pelo menos uma categoria.");
        }
        List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategoriaIds());
        for (Categoria cat : categorias) {
            if (!temAcessoCategoria(professor, cat)) {
                throw new BusinessException("Categoria " + cat.getId() + " não pertence a você e não foi compartilhada.");
            }
        }

        if (dto.getAlternativas() == null || dto.getAlternativas().size() < 2) {
            throw new BusinessException("É necessário cadastrar pelo menos duas alternativas.");
        }

        Questao questao = new Questao();
        questao.setProfessor(professor);
        questao.setTipoQuestao(dto.getTipoQuestao());
        questao.setFoto(dto.getFoto());
        questao.setEnunciado(dto.getEnunciado());
        questao.setCriadoPor(professor.getNome());
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
            long countTrue = dto.getAlternativas().stream()
                    .filter(QuestaoRequestDTO.AlternativaDTO::getVerdadeira).count();
            if (countTrue != 1) {
                throw new BusinessException("Para questão de única escolha, deve haver exatamente uma alternativa verdadeira.");
            }
        }
        return new QuestaoResponseDTO(questao);
    }

    @Transactional(readOnly = true)
    public List<QuestaoResponseDTO> listarPorProfessor(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));
        return questaoRepository.findByProfessor(professor).stream()
                .map(QuestaoResponseDTO::new).collect(Collectors.toList());
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

        if (!questao.getProfessor().getId().equals(professor.getId())) {
            throw new BusinessException("Apenas o criador pode editar esta questão.");
        }

        if (dto.getCategoriaIds() == null || dto.getCategoriaIds().isEmpty()) {
            throw new BusinessException("É necessário selecionar pelo menos uma categoria.");
        }
        List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategoriaIds());
        for (Categoria cat : categorias) {
            if (!temAcessoCategoria(professor, cat)) {
                throw new BusinessException("Categoria " + cat.getId() + " não é acessível.");
            }
        }
        questao.setCategorias(new java.util.HashSet<>(categorias));

        questao.setFoto(dto.getFoto());
        questao.setEnunciado(dto.getEnunciado());

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
            long countTrue = dto.getAlternativas().stream()
                    .filter(QuestaoRequestDTO.AlternativaDTO::getVerdadeira).count();
            if (countTrue != 1) {
                throw new BusinessException("Para questão de única escolha, deve haver exatamente uma alternativa verdadeira.");
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

        if (!temAcessoCategoria(professor, categoria)) {
            throw new BusinessException("Você não tem acesso a esta categoria.");
        }
        return questaoRepository.findByCategorias_Id(categoriaId).stream()
                .map(QuestaoResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestaoResponseDTO> listarPorCategorias(List<Long> categoriaIds, Long professorId) {
        if (categoriaIds == null || categoriaIds.isEmpty()) {
            return listarPorProfessor(professorId);
        }
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado."));

        List<Categoria> categorias = categoriaRepository.findAllById(categoriaIds);
        for (Categoria cat : categorias) {
            if (!temAcessoCategoria(professor, cat)) {
                throw new BusinessException("Sem acesso à categoria " + cat.getId());
            }
        }

        return questaoRepository.findDistinctByCategoriasIdIn(categoriaIds).stream()
                .filter(q -> {
                    if (q.getProfessor().getId().equals(professor.getId())) return true;
                    return q.getCategorias().stream().anyMatch(c ->
                            c.getCompartilhadaCom().stream()
                                    .anyMatch(p -> p.getId().equals(professor.getId())));
                })
                .map(QuestaoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void excluir(Long id, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));
        Questao questao = questaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questão não encontrada. Id: " + id));

        if (!questao.getProfessor().getId().equals(professor.getId())) {
            throw new BusinessException("Apenas o criador pode excluir esta questão.");
        }
        if (questaoAtividadeRepository.existsByQuestao(questao)) {
            throw new BusinessException("Esta questão já está sendo usada em uma ou mais atividades.");
        }
        alternativaRepository.deleteByQuestao(questao);
        questaoRepository.delete(questao);
    }
}