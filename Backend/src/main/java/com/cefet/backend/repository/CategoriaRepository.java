package com.cefet.backend.repository;

import com.cefet.backend.entity.Categoria;
import com.cefet.backend.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNomeAndCriador(String nome, Professor criador);

    List<Categoria> findByCriador(Professor criador);

    @Query("SELECT c FROM Categoria c WHERE c.criador = :professor OR :professor MEMBER OF c.compartilhadaCom")
    List<Categoria> findAllAcessiveis(@Param("professor") Professor professor);
}