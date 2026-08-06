package com.cefet.backend.dto;

import com.cefet.backend.entity.Alternativa;
import com.cefet.backend.entity.Questao;
import com.cefet.backend.entity.TipoQuestao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class QuestaoResponseDTO {

    private Long id;
    private TipoQuestao tipoQuestao;
    private String enunciado;
    private String criadoPor;
    private BigDecimal valorPontos;
    private Long professorId;
    private String professorNome;
    private List<CategoriaResponseDTO> categorias;
    private List<AlternativaDTO> alternativas;

    public QuestaoResponseDTO(Questao questao) {
        this.id = questao.getId();
        this.tipoQuestao = questao.getTipoQuestao();
        this.enunciado = questao.getEnunciado();
        this.criadoPor = questao.getCriadoPor();
        this.valorPontos = questao.getValorPontos();
        if (questao.getProfessor() != null) {
            this.professorId = questao.getProfessor().getId();
            this.professorNome = questao.getProfessor().getNome();
        }
        if (questao.getCategorias() != null) {
            this.categorias = questao.getCategorias().stream()
                    .map(CategoriaResponseDTO::new)
                    .collect(Collectors.toList());
        }
        if (questao.getAlternativas() != null) {
            this.alternativas = questao.getAlternativas().stream()
                    .map(AlternativaDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AlternativaDTO {
        private Long id;
        private String texto;
        private Boolean verdadeira;

        public AlternativaDTO(Alternativa alt) {
            this.id = alt.getId();
            this.texto = alt.getTexto();
            this.verdadeira = alt.getVerdadeira();
        }
    }
}