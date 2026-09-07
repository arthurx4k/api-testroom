package com.cefet.backend.repository;

import com.cefet.backend.entity.Atividade;
import com.cefet.backend.entity.Professor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {

    List<Atividade> findByProfessorOrderByDataGeracaoDesc(Professor professor);

}
