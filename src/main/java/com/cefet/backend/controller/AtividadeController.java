package com.cefet.backend.controller;

import com.cefet.backend.dto.AtividadeComQuestoesRequestDTO;
import com.cefet.backend.dto.AtividadeLayoutDTO;
import com.cefet.backend.dto.AtividadeRequestDTO;
import com.cefet.backend.dto.AtividadeResponseDTO;
import com.cefet.backend.dto.AtividadeResumoDTO;
import com.cefet.backend.dto.PdfOptionsDTO;
import com.cefet.backend.entity.Atividade;
import com.cefet.backend.service.AtividadeService;
import com.cefet.backend.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.cefet.backend.dto.PdfOptionsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/atividades")
@Tag(name = "Atividade")
@CrossOrigin(origins = "*")
public class AtividadeController {

    @Autowired
    private AtividadeService atividadeService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private com.cefet.backend.service.HtmlPdfService htmlPdfService;

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Exportar atividade para PDF")
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable Long id,
            @ModelAttribute PdfOptionsDTO options) throws IOException {
        Atividade atividade = atividadeService.buscarPorId(id);
        byte[] pdf = pdfService.gerarPdfAtividade(atividade, options);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "atividade_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar atividade por ID")
    public ResponseEntity<AtividadeResponseDTO> buscarPorId(@PathVariable Long id) {
        Atividade atividade = atividadeService.buscarPorId(id);
        return ResponseEntity.ok(new AtividadeResponseDTO(atividade));
    }

    @PostMapping("/criar-com-questoes")
    @Operation(summary = "Criar atividade com questões selecionadas manualmente e múltiplas versões")
    public ResponseEntity<List<AtividadeResponseDTO>> criarComQuestoes(
            @Valid @RequestBody AtividadeComQuestoesRequestDTO dto,
            @RequestParam Long professorId) {
        List<Atividade> versoes = atividadeService.criarAtividadeComQuestoes(dto, professorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(versoes.stream().map(AtividadeResponseDTO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}/gabarito")
    @Operation(summary = "Exportar gabarito da atividade para PDF")
    public ResponseEntity<byte[]> exportarGabarito(
            @PathVariable Long id,
            @ModelAttribute PdfOptionsDTO options) throws IOException {
        Atividade atividade = atividadeService.buscarPorId(id);
        byte[] pdf = pdfService.gerarGabarito(atividade, options);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "gabarito_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping
    @Operation(summary = "Listar atividades de um professor")
    public ResponseEntity<List<AtividadeResumoDTO>> listar(@RequestParam Long professorId) {
        return ResponseEntity.ok(atividadeService.listarPorProfessor(professorId));
    }

    @PutMapping("/{id}/layout")
    @Operation(summary = "Salvar layout do PDF (ordem, quebras, opções visuais)")
    public ResponseEntity<Void> salvarLayout(
            @PathVariable Long id,
            @RequestBody AtividadeLayoutDTO dto) {
        atividadeService.salvarLayout(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir atividade")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        atividadeService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/html")
    @Operation(summary = "Obter HTML (editor tipo Word) da atividade")
    public ResponseEntity<com.cefet.backend.dto.AtividadeHtmlDTO> obterHtml(@PathVariable Long id) {
        String html = atividadeService.buscarHtml(id);
        return ResponseEntity.ok(new com.cefet.backend.dto.AtividadeHtmlDTO(html));
    }

    @PutMapping("/{id}/html")
    @Operation(summary = "Salvar HTML (editor tipo Word) da atividade")
    public ResponseEntity<Void> salvarHtml(
            @PathVariable Long id,
            @RequestBody com.cefet.backend.dto.AtividadeHtmlDTO dto) {
        atividadeService.salvarHtml(id, dto.getHtml());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf-html")
    @Operation(summary = "Gerar PDF do HTML salvo (editor tipo Word)")
    public ResponseEntity<byte[]> pdfHtml(@PathVariable Long id) throws IOException {
        String html = atividadeService.buscarHtml(id);
        byte[] pdf = htmlPdfService.renderizar(html);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "atividade_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}