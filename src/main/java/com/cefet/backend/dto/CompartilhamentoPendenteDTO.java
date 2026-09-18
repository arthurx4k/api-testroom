package com.cefet.backend.dto;

import com.cefet.backend.entity.CompartilhamentoCategoria;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @NoArgsConstructor
public class CompartilhamentoPendenteDTO {
    private Long id;
    private Long categoriaId;
    private String categoriaNome;
    private String categoriaDescricao;
    private Long origemId;
    private String origemNome;
    private String origemEspecialidade;
    private String origemFoto;
    private LocalDateTime dataCompartilhamento;

    public CompartilhamentoPendenteDTO(CompartilhamentoCategoria c) {
        this.id = c.getId();
        this.categoriaId = c.getCategoria().getId();
        this.categoriaNome = c.getCategoria().getNome();
        this.categoriaDescricao = c.getCategoria().getDescricao();
        this.origemId = c.getOrigem().getId();
        this.origemNome = c.getOrigem().getNome();
        this.origemEspecialidade = c.getOrigem().getEspecialidade();
        this.origemFoto = c.getOrigem().getFoto();
        this.dataCompartilhamento = c.getDataCompartilhamento();
    }
}