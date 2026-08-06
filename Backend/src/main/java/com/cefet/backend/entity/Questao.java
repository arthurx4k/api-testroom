package com.cefet.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "questao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_questao", nullable = false)
    private TipoQuestao tipoQuestao;

    @Column(nullable = false, length = 2000)
    private String enunciado;

    @Column(name = "criado_por", length = 255)
    private String criadoPor;

    @Column(name = "valor_pontos", nullable = false, precision = 3, scale = 2)
    private BigDecimal valorPontos = BigDecimal.ONE;

    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alternativa> alternativas = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "categoria_questao",
        joinColumns = @JoinColumn(name = "questao_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();
}