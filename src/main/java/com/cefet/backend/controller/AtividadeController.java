package com.cefet.backend.controller;

import com.cefet.backend.dto.AtividadeComQuestoesRequestDTO;
import com.cefet.backend.dto.AtividadeResponseDTO;
import com.cefet.backend.dto.AtividadeResumoDTO;
import com.cefet.backend.entity.Atividade;
import com.cefet.backend.service.AtividadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private com.cefet.backend.service.HtmlPdfService htmlPdfService;

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

    @GetMapping
    @Operation(summary = "Listar atividades de um professor")
    public ResponseEntity<List<AtividadeResumoDTO>> listar(@RequestParam Long professorId) {
        return ResponseEntity.ok(atividadeService.listarPorProfessor(professorId));
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
    public ResponseEntity<byte[]> pdfHtml(
            @PathVariable Long id,
            @RequestParam(name = "tipo", defaultValue = "prova") String tipo) throws IOException {
        boolean gabarito = "gabarito".equalsIgnoreCase(tipo);
        String html = atividadeService.buscarHtml(id);
        byte[] pdf = htmlPdfService.renderizar(html, gabarito);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String nome = (gabarito ? "gabarito_" : "atividade_") + id + ".pdf";
        headers.setContentDispositionFormData("inline", nome);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}