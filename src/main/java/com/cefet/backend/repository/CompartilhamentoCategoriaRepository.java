package com.cefet.backend.repository;

import com.cefet.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CompartilhamentoCategoriaRepository
        extends JpaRepository<CompartilhamentoCategoria, Long> {

    List<CompartilhamentoCategoria> findByDestinoAndStatus(
            Professor destino, StatusCompartilhamento status);

    Optional<CompartilhamentoCategoria> findByCategoriaAndDestino(
            Categoria categoria, Professor destino);
}