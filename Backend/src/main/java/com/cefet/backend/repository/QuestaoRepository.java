package com.cefet.backend.repository;

import com.cefet.backend.entity.Professor;
import com.cefet.backend.entity.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    List<Questao> findByProfessor(Professor professor);
    List<Questao> findByCategorias_Id(Long categoriaId);
}