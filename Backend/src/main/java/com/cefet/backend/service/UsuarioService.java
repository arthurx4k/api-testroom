package com.cefet.backend.service;

import com.cefet.backend.dto.UsuarioRequestDTO;
import com.cefet.backend.dto.UsuarioResponseDTO;
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
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()) != null) {
            throw new BusinessException("E-mail já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setCargo(dto.getCargo());

        usuario = usuarioRepository.save(usuario);

        if (dto.getCargo() == CargoUsuario.PROFESSOR) {
            Professor professor = new Professor();
            professor.setUsuario(usuario);
            professor.setNome(usuario.getEmail());
            professorRepository.save(professor);
        }

        return new UsuarioResponseDTO(usuario);
    }
}