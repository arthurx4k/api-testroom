package com.cefet.backend.repository;

import com.cefet.backend.entity.Professor;
import com.cefet.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUsuario(Usuario usuario);
}