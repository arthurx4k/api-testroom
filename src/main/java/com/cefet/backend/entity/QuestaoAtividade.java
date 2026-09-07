package com.cefet.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "questao_atividade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestaoAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "atividade_id", nullable = false)
    private Atividade atividade;

    @ManyToOne
    @JoinColumn(name = "questao_id", nullable = false)
    private Questao questao;

    @Column(name = "posicao_questao", nullable = false)
    private Integer posicao;

    @Column(name = "valor_pontos", nullable = false, precision = 3, scale = 2)
    private BigDecimal valorPontos;
}