package com.cefet.backend.dto;

import com.cefet.backend.entity.Categoria;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter @NoArgsConstructor
public class CategoriaResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Long criadorId;
    private String criadorNome;      
    private String criadorFoto;      
    private Set<Long> compartilhadaComIds;
    private Set<String> compartilhadaComNomes;

    public CategoriaResponseDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nome = categoria.getNome();
        this.descricao = categoria.getDescricao();

        if (categoria.getCriador() != null) {                     
            this.criadorId   = categoria.getCriador().getId();
            this.criadorNome = categoria.getCriador().getNome();
            this.criadorFoto = categoria.getCriador().getFoto();
        }
        if (categoria.getCompartilhadaCom() != null) {
            this.compartilhadaComIds = categoria.getCompartilhadaCom().stream()
                    .map(p -> p.getId()).collect(Collectors.toSet());
            this.compartilhadaComNomes = categoria.getCompartilhadaCom().stream()
                    .map(p -> p.getNome()).collect(Collectors.toSet());
        }
    }
}