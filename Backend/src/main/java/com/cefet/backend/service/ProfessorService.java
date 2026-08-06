package com.cefet.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cefet.backend.dto.ProfessorRequestDTO;
import com.cefet.backend.dto.ProfessorResponseDTO;
import com.cefet.backend.entity.Professor;
import com.cefet.backend.entity.Usuario;
import com.cefet.backend.exception.BusinessException;
import com.cefet.backend.exception.ResourceNotFoundException;
import com.cefet.backend.repository.ProfessorRepository;
import com.cefet.backend.repository.UsuarioRepository;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public ProfessorResponseDTO criar(ProfessorRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário com ID " + dto.getUsuarioId() + " não encontrado"));

        if (usuario.getProfessor() != null) {
            throw new BusinessException("Professor já cadastrado.");
        }

        Professor professor = new Professor();
        professor.setUsuario(usuario);
        professor.setNome(dto.getNome());
        professor.setEspecialidade(dto.getEspecialidade());
        professor.setDescricao(dto.getDescricao());

        professor = professorRepository.save(professor);
        return new ProfessorResponseDTO(professor);
    }

    public List<ProfessorResponseDTO> listarTodos() {
        return professorRepository.findAll().stream()
                .map(ProfessorResponseDTO::new)
                .collect(Collectors.toList());
    }

    public ProfessorResponseDTO buscarPorId(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor com ID " + id + " não encontrado"));
        return new ProfessorResponseDTO(professor);
    }

    @Transactional
    public ProfessorResponseDTO atualizar(Long id, ProfessorRequestDTO dto) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor com ID " + id + " não encontrado"));

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().equals(professor.getUsuario().getId())) {
            Usuario novoUsuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuário com ID " + dto.getUsuarioId() + " não encontrado"));
            if (novoUsuario.getProfessor() != null && !novoUsuario.getProfessor().getId().equals(id)) {
                throw new BusinessException("Este usuário já está vinculado a outro professor.");
            }
            professor.setUsuario(novoUsuario);
        }

        professor.setNome(dto.getNome());
        professor.setEspecialidade(dto.getEspecialidade());
        professor.setDescricao(dto.getDescricao());

        professor = professorRepository.save(professor);
        return new ProfessorResponseDTO(professor);
    }

    @Transactional
    public Professor atualizarNome(Long id, String nome) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));
        professor.setNome(nome);
        return professorRepository.save(professor);
    }

    @Transactional
    public void deletar(Long id) {
        if (!professorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Professor com ID " + id + " não encontrado");
        }
        professorRepository.deleteById(id);
    }
}