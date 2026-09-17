package com.cefet.backend.service;

import com.cefet.backend.entity.Alternativa;
import com.cefet.backend.entity.Atividade;
import com.cefet.backend.entity.QuestaoAtividade;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    public byte[] gerarPdfAtividade(Atividade atividade) throws IOException {
        return gerarPdf(atividade, false);
    }

    public byte[] gerarGabarito(Atividade atividade) throws IOException {
        return gerarPdf(atividade, true);
    }

    private byte[] gerarPdf(Atividade atividade, boolean comGabarito) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setMargins(36, 36, 36, 36);

        String titulo = comGabarito ? "GABARITO — " + atividade.getTitulo() : atividade.getTitulo();
        document.add(new Paragraph(titulo)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        if (atividade.getDescricao() != null && !atividade.getDescricao().isEmpty()) {
            document.add(new Paragraph(atividade.getDescricao()));
        }

        String data = atividade.getDataGeracao() != null
                ? atividade.getDataGeracao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "Data não definida";
        document.add(new Paragraph("Gerada em: " + data));

        if (atividade.getInstrucoes() != null && !atividade.getInstrucoes().isEmpty()) {
            document.add(new Paragraph("Instruções: " + atividade.getInstrucoes()));
        }

        document.add(new Paragraph("\n"));

        List<QuestaoAtividade> questoes = atividade.getQuestoes().stream()
                .sorted(Comparator.comparing(QuestaoAtividade::getPosicao))
                .collect(Collectors.toList());

        for (QuestaoAtividade qa : questoes) {
            document.add(new Paragraph(qa.getPosicao() + ". " + qa.getQuestao().getEnunciado())
                    .setFontSize(12));

            List<Alternativa> alternativas = obterAlternativasOrdenadas(qa);

            char letra = 'A';
            for (Alternativa alt : alternativas) {
                String texto = "   " + letra + ") " + alt.getTexto();
                if (comGabarito && Boolean.TRUE.equals(alt.getVerdadeira())) {
                    texto += "    <<< RESPOSTA CORRETA";
                }
                document.add(new Paragraph(texto).setFontSize(11));
                letra++;
            }
            document.add(new Paragraph(" "));
        }

        document.close();
        return baos.toByteArray();
    }

    private List<Alternativa> obterAlternativasOrdenadas(QuestaoAtividade qa) {
        List<Alternativa> alternativas = new ArrayList<>(qa.getQuestao().getAlternativas());
        String ordemStr = qa.getOrdemAlternativas();
        if (ordemStr != null && !ordemStr.isBlank()) {
            List<Long> ordemIds = Arrays.stream(ordemStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            alternativas.sort(Comparator.comparingInt(a -> {
                int idx = ordemIds.indexOf(a.getId());
                return idx >= 0 ? idx : Integer.MAX_VALUE;
            }));
        }
        return alternativas;
    }
}