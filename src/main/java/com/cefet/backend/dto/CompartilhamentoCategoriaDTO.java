package com.cefet.backend.dto;

import com.cefet.backend.entity.CompartilhamentoCategoria;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CompartilhamentoCategoriaDTO {
    private Long id;
    private Long categoriaId;
    private Long origemId;
    private String origemNome;
    private Long destinoId;
    private String destinoNome;
    private String destinoFoto;
    private String status;
    private LocalDateTime dataCompartilhamento;

    public CompartilhamentoCategoriaDTO(CompartilhamentoCategoria c) {
        this.id = c.getId();
        this.categoriaId = c.getCategoria().getId();
        this.origemId = c.getOrigem().getId();
        this.origemNome = c.getOrigem().getNome();
        this.destinoId = c.getDestino().getId();
        this.destinoNome = c.getDestino().getNome();
        this.destinoFoto = c.getDestino().getFoto();
        this.status = c.getStatus().name();
        this.dataCompartilhamento = c.getDataCompartilhamento();
    }
}