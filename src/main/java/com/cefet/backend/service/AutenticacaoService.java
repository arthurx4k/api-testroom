package com.cefet.backend.service;

import com.cefet.backend.dto.AutenticacaoRequestDTO;
import com.cefet.backend.dto.AutenticacaoResponseDTO;
import com.cefet.backend.entity.CargoUsuario;
import com.cefet.backend.entity.Professor;
import com.cefet.backend.entity.Usuario;
import com.cefet.backend.exception.BusinessException;
import com.cefet.backend.repository.ProfessorRepository;
import com.cefet.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Transactional
    public AutenticacaoResponseDTO autenticar(AutenticacaoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmailAndSenha(dto.getEmail(), dto.getSenha());
        if (usuario == null) {
            throw new BusinessException("Login e/ou senha inválidos.");
        }

        AutenticacaoResponseDTO response = new AutenticacaoResponseDTO(usuario);
        if (usuario.getCargo() == CargoUsuario.PROFESSOR) {
            Professor professor = professorRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new BusinessException("Professor não encontrado para este usuário."));
            response.setProfessorId(professor.getId());
            response.setProfessorNome(professor.getNome());
            response.setFoto(professor.getFoto());
        }
        return response;
    }
    
}