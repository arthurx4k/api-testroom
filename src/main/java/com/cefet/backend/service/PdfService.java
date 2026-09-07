package com.cefet.backend.service;

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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    public byte[] gerarPdfAtividade(Atividade atividade) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setMargins(36, 36, 36, 36);

        Paragraph titulo = new Paragraph(atividade.getTitulo())
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);

        if (atividade.getDescricao() != null && !atividade.getDescricao().isEmpty()) {
            document.add(new Paragraph(atividade.getDescricao()));
        }

        // Data
        String data = atividade.getDataGeracao() != null ?
                atividade.getDataGeracao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) :
                "Data não definida";
        document.add(new Paragraph("Gerada em: " + data));

        if (atividade.getInstrucoes() != null && !atividade.getInstrucoes().isEmpty()) {
            document.add(new Paragraph("Instruções: " + atividade.getInstrucoes()));
        }

        document.add(new Paragraph("\n"));

        List<QuestaoAtividade> questoes = atividade.getQuestoes().stream()
                .sorted(Comparator.comparing(QuestaoAtividade::getPosicao))
                .collect(Collectors.toList());

        for (QuestaoAtividade qa : questoes) {
            Paragraph questaoPar = new Paragraph(qa.getPosicao() + ". " + qa.getQuestao().getEnunciado())
                    .setFontSize(12);
            document.add(questaoPar);

            List<com.cefet.backend.entity.Alternativa> alternativas = qa.getQuestao().getAlternativas();
            java.util.Collections.shuffle(alternativas);

            char letra = 'A';
            for (com.cefet.backend.entity.Alternativa alt : alternativas) {
                Paragraph altPar = new Paragraph("   " + letra + ") " + alt.getTexto())
                        .setFontSize(11);
                document.add(altPar);
                letra++;
            }
            document.add(new Paragraph(" "));
        }

        document.close();
        return baos.toByteArray();
    }
}