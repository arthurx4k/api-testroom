package com.cefet.backend.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class HtmlPdfService {

    public byte[] renderizar(String html) throws IOException {
        if (html == null || html.isBlank()) {
            html = "<!DOCTYPE html><html><body><p>Sem conteudo.</p></body></html>";
        }

        Document doc = Jsoup.parse(html);
        doc.outputSettings()
           .syntax(Document.OutputSettings.Syntax.xml) 
           .escapeMode(Entities.EscapeMode.xhtml)       
           .charset("UTF-8")
           .prettyPrint(false);
        String xhtml = doc.outerHtml();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(xhtml, null);
        builder.toStream(baos);
        try {
            builder.run();
        } catch (Exception e) {
            throw new IOException("Falha ao renderizar HTML em PDF: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }
}