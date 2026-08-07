package com.cefet.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessorRequestDTO {

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotBlank(message = "O nome do professor é obrigatório")
    private String nome;

    private String especialidade;

    private String descricao;
}