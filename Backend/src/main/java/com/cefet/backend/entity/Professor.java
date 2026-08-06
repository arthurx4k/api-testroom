package com.cefet.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "professor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 255)
    private String especialidade;

    @Column(length = 255)
    private String descricao;

    @ManyToMany(mappedBy = "compartilhadaCom")
    private Set<Categoria> categoriasCompartilhadas = new HashSet<>();

    @Column(name = "foto", length = 500, nullable = true)
    private String foto;
}