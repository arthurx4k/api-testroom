package com.cefet.backend.dto;

import com.cefet.backend.entity.TipoQuestao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuestaoRequestDTO {

    @NotNull(message = "Tipo de questão é obrigatório")
    private TipoQuestao tipoQuestao;

    @NotBlank(message = "Enunciado é obrigatório")
    private String enunciado;

    @Positive(message = "Valor de pontos deve ser maior que zero")
    private BigDecimal valorPontos = BigDecimal.ONE;

    @NotNull(message = "É necessário selecionar pelo menos uma categoria")
    private List<Long> categoriaIds = new ArrayList<>();

    @Valid
    private List<AlternativaDTO> alternativas = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AlternativaDTO {
        @NotBlank(message = "Texto da alternativa é obrigatório")
        private String texto;

        @NotNull(message = "Indicador de verdadeiro/falso é obrigatório")
        private Boolean verdadeira;
    }
}