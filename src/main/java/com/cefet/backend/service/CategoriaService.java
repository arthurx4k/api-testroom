package com.cefet.backend.service;

import com.cefet.backend.dto.CategoriaRequestDTO;
import com.cefet.backend.dto.CategoriaResponseDTO;
import com.cefet.backend.dto.CompartilhamentoPendenteDTO;
import com.cefet.backend.entity.Categoria;
import com.cefet.backend.entity.CompartilhamentoCategoria;
import com.cefet.backend.entity.Professor;
import com.cefet.backend.entity.StatusCompartilhamento;
import com.cefet.backend.exception.BusinessException;
import com.cefet.backend.exception.ResourceNotFoundException;
import com.cefet.backend.repository.CategoriaRepository;
import com.cefet.backend.repository.CompartilhamentoCategoriaRepository;
import com.cefet.backend.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Transactional
    public CategoriaResponseDTO inserir(CategoriaRequestDTO dto, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));

        if (categoriaRepository.existsByNomeAndCriador(dto.getNome(), professor)) {
            throw new BusinessException("Já existe uma categoria com este nome criada por você.");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setCriador(professor);

        return new CategoriaResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarAcessiveis(Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));

        List<Categoria> categorias = categoriaRepository.findAllAcessiveis(professor);
        return categorias.stream().map(CategoriaResponseDTO::new).toList();
    }

    @Transactional
    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. Id: " + id));
        return new CategoriaResponseDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. Id: " + id));

        if (!categoria.getCriador().equals(professor)) {
            throw new BusinessException("Apenas o criador pode editar a categoria.");
        }

        if (!categoria.getNome().equals(dto.getNome()) &&
                categoriaRepository.existsByNomeAndCriador(dto.getNome(), professor)) {
            throw new BusinessException("Já existe uma categoria com este nome para você.");
        }

        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        return new CategoriaResponseDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public void excluir(Long id, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado. Id: " + professorId));

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. Id: " + id));

        if (!categoria.getCriador().equals(professor)) {
            throw new BusinessException("Apenas o criador pode excluir a categoria.");
        }

        categoriaRepository.deleteById(id);
    }

    @Transactional
    public void descompartilhar(Long categoriaId, Long professorAlvoId, Long professorOrigemId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. Id: " + categoriaId));

        Professor origem = professorRepository.findById(professorOrigemId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor origem não encontrado."));

        if (!categoria.getCriador().equals(origem)) {
            throw new BusinessException("Apenas o criador pode remover o compartilhamento.");
        }

        Professor alvo = professorRepository.findById(professorAlvoId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor alvo não encontrado."));

        if (!categoria.getCompartilhadaCom().contains(alvo)) {
            throw new BusinessException("Categoria não está compartilhada com este professor.");
        }

        categoria.getCompartilhadaCom().remove(alvo);
        categoriaRepository.save(categoria);
    }

    @Autowired
    private CompartilhamentoCategoriaRepository compartilhamentoRepo;

    @Transactional
    public void compartilhar(Long categoriaId, Long origemId, Long destinoId) {
        Categoria cat = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        Professor origem = professorRepository.findById(origemId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor origem não encontrado"));
        Professor destino = professorRepository.findById(destinoId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor destino não encontrado"));

        if (!cat.getCriador().getId().equals(origem.getId()))
            throw new BusinessException("Você só pode compartilhar categorias que criou.");
        if (origem.getId().equals(destino.getId()))
            throw new BusinessException("Não é possível compartilhar consigo mesmo.");
        if (cat.getCompartilhadaCom().contains(destino))
            throw new BusinessException("Categoria já compartilhada com este professor.");

        compartilhamentoRepo.findByCategoriaAndDestino(cat, destino).ifPresent(c -> {
            if (c.getStatus() == StatusCompartilhamento.PENDENTE)
                throw new BusinessException("Já existe um convite pendente para este professor.");
            if (c.getStatus() == StatusCompartilhamento.ACEITO)
                throw new BusinessException("Categoria já aceita por este professor.");
        });

        CompartilhamentoCategoria c = new CompartilhamentoCategoria();
        c.setCategoria(cat);
        c.setOrigem(origem);
        c.setDestino(destino);
        c.setDataCompartilhamento(LocalDateTime.now());
        c.setStatus(StatusCompartilhamento.PENDENTE);
        compartilhamentoRepo.save(c);
    }

    @Transactional
    public void aceitar(Long compartilhamentoId, Long professorId) {
        CompartilhamentoCategoria c = compartilhamentoRepo.findById(compartilhamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Compartilhamento não encontrado"));
        if (!c.getDestino().getId().equals(professorId))
            throw new BusinessException("Sem permissão.");
        if (c.getStatus() != StatusCompartilhamento.PENDENTE)
            throw new BusinessException("Compartilhamento já processado.");

        c.setStatus(StatusCompartilhamento.ACEITO);
        c.getCategoria().getCompartilhadaCom().add(c.getDestino());
        compartilhamentoRepo.save(c);
    }

    @Transactional
    public void recusar(Long compartilhamentoId, Long professorId) {
        CompartilhamentoCategoria c = compartilhamentoRepo.findById(compartilhamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Compartilhamento não encontrado"));
        if (!c.getDestino().getId().equals(professorId))
            throw new BusinessException("Sem permissão.");
        if (c.getStatus() != StatusCompartilhamento.PENDENTE)
            throw new BusinessException("Compartilhamento já processado.");
        c.setStatus(StatusCompartilhamento.RECUSADO);
        compartilhamentoRepo.save(c);
    }

    public List<CompartilhamentoPendenteDTO> listarPendentes(Long professorId) {
        Professor p = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));
        return compartilhamentoRepo
                .findByDestinoAndStatus(p, StatusCompartilhamento.PENDENTE)
                .stream().map(CompartilhamentoPendenteDTO::new).toList();
    }
}