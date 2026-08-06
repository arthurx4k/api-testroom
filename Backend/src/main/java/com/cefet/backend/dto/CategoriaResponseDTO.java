package com.cefet.backend.dto;

import com.cefet.backend.entity.Categoria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CategoriaResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Long criadorId;
    private Set<Long> compartilhadaComIds; 

    public CategoriaResponseDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nome = categoria.getNome();
        this.descricao = categoria.getDescricao();
        this.criadorId = categoria.getCriador().getId();
        if (categoria.getCompartilhadaCom() != null) {
            this.compartilhadaComIds = categoria.getCompartilhadaCom().stream()
                    .map(p -> p.getId())
                    .collect(Collectors.toSet());
        }
    }
}