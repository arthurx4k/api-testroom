package com.cefet.backend.dto;

import com.cefet.backend.entity.Atividade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AtividadeResumoDTO {
    private Long id;
    private String titulo;
    private BigDecimal valorPontos;
    private LocalDateTime dataGeracao;
    private Integer quantidadeVersoes;
    private String grupoId;
    private int quantidadeQuestoes;

    public AtividadeResumoDTO(Atividade a) {
        this.id = a.getId();
        this.titulo = a.getTitulo();
        this.valorPontos = a.getValorPontos();
        this.dataGeracao = a.getDataGeracao();
        this.quantidadeVersoes = a.getQuantidadeVersoes();
        this.grupoId = a.getGrupoId();
        this.quantidadeQuestoes = a.getQuestoes() != null ? a.getQuestoes().size() : 0;
    }
}