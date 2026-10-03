package com.cefet.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PdfOptionsDTO {
    
    private Boolean manterQuestoesJuntas = true;
    private Boolean mostrarDescricao = true;
    private Boolean mostrarInstrucoes = true;
    private Boolean mostrarData = true;
    private Boolean mostrarTotalPontos = true;
    private Boolean mostrarPontosPorQuestao = true;
    private Boolean mostrarFotos = true;

    private Float tamanhoFonteTitulo = 18f;
    private Float tamanhoFonteEnunciado = 12f;
    private Float tamanhoFonteAlternativa = 11f;
    private Float tamanhoFonteInfo = 12f;
    private Float espacamentoEntreQuestoes = 10f;
    private Float margemPagina = 36f;
}