package com.cefet.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 2000)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "criador_id", nullable = false)
    private Professor criador;

    @ManyToMany
    @JoinTable(
        name = "categoria_compartilhada",
        joinColumns = @JoinColumn(name = "categoria_id"),
        inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    @JsonIgnore
    private Set<Professor> compartilhadaCom = new HashSet<>();

    @ManyToMany(mappedBy = "categorias")
    @JsonIgnore
    private Set<Questao> questoes = new HashSet<>();
}