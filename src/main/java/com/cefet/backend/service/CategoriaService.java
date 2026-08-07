package com.cefet.backend.service;

import com.cefet.backend.dto.CategoriaRequestDTO;
import com.cefet.backend.dto.CategoriaResponseDTO;
import com.cefet.backend.entity.Categoria;
import com.cefet.backend.entity.Professor;
import com.cefet.backend.exception.BusinessException;
import com.cefet.backend.exception.ResourceNotFoundException;
import com.cefet.backend.repository.CategoriaRepository;
import com.cefet.backend.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void compartilhar(Long categoriaId, Long professorAlvoId, Long professorOrigemId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. Id: " + categoriaId));

        Professor origem = professorRepository.findById(professorOrigemId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor origem não encontrado."));

        if (!categoria.getCriador().equals(origem)) {
            throw new BusinessException("Apenas o criador pode compartilhar a categoria.");
        }

        Professor alvo = professorRepository.findById(professorAlvoId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor alvo não encontrado."));

        if (categoria.getCompartilhadaCom().contains(alvo)) {
            throw new BusinessException("Categoria já compartilhada com este professor.");
        }

        categoria.getCompartilhadaCom().add(alvo);
        categoriaRepository.save(categoria);
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
}