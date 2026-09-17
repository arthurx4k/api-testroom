package com.cefet.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AtividadeComQuestoesRequestDTO {
    @NotBlank
    private String titulo;
    private String descricao;
    private String instrucoes;

    @NotNull
    private List<QuestaoSelecionadaDTO> questoes;

    @Positive
    private Integer quantidadeVersoes = 1;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class QuestaoSelecionadaDTO {
        @NotNull
        private Long questaoId;
        
        @Positive
        @NotNull(message = "Valor de pontos é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser positivo")
        private BigDecimal valorPontos;
    }
}