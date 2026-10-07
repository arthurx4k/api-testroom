package com.cefet.backend.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class HtmlPdfService {

    public byte[] renderizar(String html) throws IOException {
        if (html == null || html.isBlank()) {
            html = "<!DOCTYPE html><html><body><p>Sem conteudo.</p></body></html>";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(html, null);
        builder.toStream(baos);
        try {
            builder.run();
        } catch (Exception e) {
            throw new IOException("Falha ao renderizar HTML em PDF: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }
}