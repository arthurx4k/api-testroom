package com.cefet.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compartilhamento_categoria",
       uniqueConstraints = @UniqueConstraint(columnNames = {"categoria_id", "destino_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CompartilhamentoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origem_id", nullable = false)
    private Professor origem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destino_id", nullable = false)
    private Professor destino;

    @Column(nullable = false)
    private LocalDateTime dataCompartilhamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCompartilhamento status;
}