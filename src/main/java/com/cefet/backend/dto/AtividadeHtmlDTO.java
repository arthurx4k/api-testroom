package com.cefet.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AtividadeHtmlDTO {
    private String html;

    public AtividadeHtmlDTO(String html) {
        this.html = html;
    }
}