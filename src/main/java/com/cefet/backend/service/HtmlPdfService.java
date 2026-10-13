package com.cefet.backend.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class HtmlPdfService {

    private static final String CSS_PDF = """
        @page { size: A4; margin: 15mm; }
        html, body { margin: 0; padding: 0; }
        body {
          font-family: Helvetica, Arial, sans-serif;
          font-size: 11pt;
          line-height: 1.45;
          color: #000;
          background: #fff;
        }
        .pagina { width: auto; padding: 0; margin: 0; }
        h1 { font-size: 20pt; margin: 0 0 10pt; }
        h2 { font-size: 16pt; margin: 12pt 0 6pt; }
        h3 { font-size: 13pt; margin: 10pt 0 4pt; }
        h4 { font-size: 12pt; margin: 8pt 0 4pt; }
        p { margin: 0 0 6pt; }
        ul, ol { margin: 0 0 6pt; padding-left: 22pt; }
        blockquote { margin: 8pt 0; padding: 4pt 10pt; border-left: 3px solid #999; color: #444; }
        img { max-width: 100%; height: auto; }
        table { border-collapse: collapse; width: 100%; margin: 6pt 0; }
        th, td { border: 1px solid #666; padding: 4pt 6pt; vertical-align: top; }
        hr { border: none; border-top: 1px solid #bbb; margin: 10pt 0; }
        p, ul, ol, table, blockquote, img, h1, h2, h3, h4, hr, tr, li {
          page-break-inside: avoid;
        }
        """;

    public byte[] renderizar(String html) throws IOException {
        return renderizar(html, false);
    }

    public byte[] renderizar(String html, boolean gabarito) throws IOException {
        if (html == null || html.isBlank()) {
            html = "<!DOCTYPE html><html><body><p>Sem conteudo.</p></body></html>";
        }

        Document doc = Jsoup.parse(html);

        doc.select("style").remove();
        Element style = doc.head().appendElement("style").attr("type", "text/css");
        style.appendChild(new DataNode(CSS_PDF));

        Element pagina = doc.selectFirst(".pagina");
        if (pagina == null) {
            pagina = new Element("div").addClass("pagina");
            pagina.html(doc.body().html());
            doc.body().empty();
            doc.body().appendChild(pagina);
        }

        doc.select(".pg-spacer").remove();

        if (gabarito) {
            pagina.addClass("gabarito");
            doc.select(".so-prova").remove();
            for (Element e : doc.select(".so-gabarito, .alt-correta")) {
                e.attr("style", "color:#0a7d2e;font-weight:bold;");
            }
            pagina.prependElement("p")
                  .attr("style", "text-align:right;font-size:9pt;font-weight:bold;color:#0a7d2e;margin:0 0 6pt;")
                  .text("GABARITO");
        } else {
            pagina.removeClass("gabarito");
            doc.select(".so-gabarito, .alt-correta").remove();
        }

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