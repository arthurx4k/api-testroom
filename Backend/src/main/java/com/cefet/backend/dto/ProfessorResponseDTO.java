package com.cefet.backend.dto;

import com.cefet.backend.entity.Professor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessorResponseDTO {

    private Long id;
    private String nome;
    private String especialidade;
    private String descricao;
    private String foto;
    private Long usuarioId;
    private String usuarioEmail;

    public ProfessorResponseDTO(Professor professor) {
    this.id = professor.getId();
    this.nome = professor.getNome();
    this.especialidade = professor.getEspecialidade();
    this.descricao = professor.getDescricao();
    this.foto = professor.getFoto();
    if (professor.getUsuario() != null) {
        this.usuarioId = professor.getUsuario().getId();
        this.usuarioEmail = professor.getUsuario().getEmail();
    }
}
}