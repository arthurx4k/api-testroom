package com.cefet.backend.service;

import com.cefet.backend.dto.PdfOptionsDTO;
import com.cefet.backend.entity.Alternativa;
import com.cefet.backend.entity.Atividade;
import com.cefet.backend.entity.QuestaoAtividade;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    public byte[] gerarPdfAtividade(Atividade atividade, PdfOptionsDTO options) throws IOException {
        return gerarPdf(atividade, options, false);
    }

    public byte[] gerarGabarito(Atividade atividade, PdfOptionsDTO options) throws IOException {
        return gerarPdf(atividade, options, true);
    }

    private byte[] gerarPdf(Atividade atividade, PdfOptionsDTO opt, boolean comGabarito) throws IOException {
        if (opt == null) opt = new PdfOptionsDTO();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        float margem = nvl(opt.getMargemPagina(), 36f);
        document.setMargins(margem, margem, margem, margem);

        // ===== Cabeçalho =====
        String titulo = comGabarito ? "GABARITO — " + atividade.getTitulo() : atividade.getTitulo();
        document.add(new Paragraph(titulo)
                .setFontSize(nvl(opt.getTamanhoFonteTitulo(), 18f))
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        if (isTrue(opt.getMostrarDescricao())
                && atividade.getDescricao() != null && !atividade.getDescricao().isBlank()) {
            document.add(new Paragraph(atividade.getDescricao()));
        }

        if (isTrue(opt.getMostrarData())) {
            String data = atividade.getDataGeracao() != null
                    ? atividade.getDataGeracao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "Data não definida";
            document.add(new Paragraph("Gerada em: " + data).setFontSize(10));
        }

        if (isTrue(opt.getMostrarInstrucoes())
                && atividade.getInstrucoes() != null && !atividade.getInstrucoes().isBlank()) {
            document.add(new Paragraph("Instruções: " + atividade.getInstrucoes()));
        }

        if (isTrue(opt.getMostrarTotalPontos())) {
            BigDecimal total = atividade.getValorPontos() != null ? atividade.getValorPontos() : BigDecimal.ZERO;
            document.add(new Paragraph("Total de pontos: " + total.stripTrailingZeros().toPlainString())
                    .setFontSize(10));
        }

        document.add(new Paragraph("\n"));

        float espacamento = nvl(opt.getEspacamentoEntreQuestoes(), 10f);
        float fEnunciado   = nvl(opt.getTamanhoFonteEnunciado(), 12f);
        float fAlternativa = nvl(opt.getTamanhoFonteAlternativa(), 11f);
        boolean manterJuntas = isTrue(opt.getManterQuestoesJuntas());
        boolean mostrarFotos = isTrue(opt.getMostrarFotos());

        List<QuestaoAtividade> questoes = atividade.getQuestoes().stream()
                .sorted(Comparator.comparing(QuestaoAtividade::getPosicao))
                .collect(Collectors.toList());

        for (QuestaoAtividade qa : questoes) {
            Div bloco = new Div();
            if (manterJuntas) {
                bloco.setKeepTogether(true);
            }
            bloco.setMarginBottom(espacamento);

            StringBuilder cab = new StringBuilder();
            cab.append(qa.getPosicao()).append(". ").append(qa.getQuestao().getEnunciado());
            if (isTrue(opt.getMostrarPontosPorQuestao()) && qa.getValorPontos() != null) {
                cab.append(" (")
                   .append(qa.getValorPontos().stripTrailingZeros().toPlainString())
                   .append(" pts)");
            }
            bloco.add(new Paragraph(cab.toString()).setFontSize(fEnunciado));

            String fotoUrl = qa.getQuestao().getFoto();
            if (mostrarFotos && fotoUrl != null && !fotoUrl.isBlank()) {
                try {
                    ImageData imgData = ImageDataFactory.create(fotoUrl);
                    Image img = new Image(imgData);
                    img.scaleToFit(300, 400);
                    img.setMarginTop(4).setMarginBottom(6);
                    bloco.add(img);
                } catch (Exception ex) {
                }
            }

            List<Alternativa> alternativas = obterAlternativasOrdenadas(qa);
            char letra = 'A';
            for (Alternativa alt : alternativas) {
                String texto = "   " + letra + ") " + alt.getTexto();
                if (comGabarito && Boolean.TRUE.equals(alt.getVerdadeira())) {
                    texto += "    <<< RESPOSTA CORRETA";
                }
                bloco.add(new Paragraph(texto).setFontSize(fAlternativa));
                letra++;
            }

            document.add(bloco);
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

    private static boolean isTrue(Boolean b)   { return b == null || b; }
    private static float nvl(Float f, float d) { return f == null ? d : f; }
}