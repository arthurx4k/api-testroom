package com.cefet.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AutenticacaoRequestDTO {

    @NotBlank(message = "O campo email é obrigatório")
    private String email;

    @NotBlank(message = "O campo senha é obrigatório")
    private String senha;
}