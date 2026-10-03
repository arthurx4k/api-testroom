package com.cefet.backend.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AtividadeLayoutDTO {
    private List<OrdemQuestaoDTO> ordem;
    private String pdfOptionsJson;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OrdemQuestaoDTO {
        private Long questaoAtividadeId;
        private Integer posicao;
        private Boolean quebraPaginaAntes;
        private String enunciadoHtml;
    }
}