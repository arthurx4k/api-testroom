package com.cefet.backend.dto;

import com.cefet.backend.entity.Atividade;
import com.cefet.backend.entity.QuestaoAtividade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class AtividadeResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String instrucoes;
    private BigDecimal valorPontos;
    private Long professorId;
    private String professorNome;
    private LocalDateTime dataGeracao;
    private List<QuestaoAtividadeDTO> questoes;

    public AtividadeResponseDTO(Atividade atividade) {
        this.id = atividade.getId();
        this.titulo = atividade.getTitulo();
        this.descricao = atividade.getDescricao();
        this.instrucoes = atividade.getInstrucoes();
        this.valorPontos = atividade.getValorPontos();
        this.professorId = atividade.getProfessor().getId();
        this.professorNome = atividade.getProfessor().getNome();
        this.dataGeracao = atividade.getDataGeracao();
        if (atividade.getQuestoes() != null) {
            this.questoes = atividade.getQuestoes().stream()
                    .map(QuestaoAtividadeDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class QuestaoAtividadeDTO {
        private Long questaoId;
        private String enunciado;
        private Integer posicao;
        private BigDecimal valorPontos;
        private List<AlternativaDTO> alternativas;

        public QuestaoAtividadeDTO(QuestaoAtividade qa) {
            this.questaoId = qa.getQuestao().getId();
            this.enunciado = qa.getQuestao().getEnunciado();
            this.posicao = qa.getPosicao();
            this.valorPontos = qa.getValorPontos();
            if (qa.getQuestao().getAlternativas() != null) {
                this.alternativas = qa.getQuestao().getAlternativas().stream()
                        .map(alt -> new AlternativaDTO(alt))
                        .collect(Collectors.toList());
                Collections.shuffle(this.alternativas);
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AlternativaDTO {
        private Long id;
        private String texto;

        public AlternativaDTO(com.cefet.backend.entity.Alternativa alt) {
            this.id = alt.getId();
            this.texto = alt.getTexto();
            
        }
    }
}