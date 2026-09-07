package com.cefet.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AtividadeRequestDTO {
    @NotBlank
    private String titulo;
    private String descricao;
    private String instrucoes;
    @Positive
    private Integer quantidade;
    private List<Long> categoriaIds; 
}