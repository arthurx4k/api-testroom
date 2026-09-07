package com.cefet.backend.controller;

import com.cefet.backend.dto.AtividadeRequestDTO;
import com.cefet.backend.dto.AtividadeResponseDTO;
import com.cefet.backend.entity.Atividade;
import com.cefet.backend.service.AtividadeService;
import com.cefet.backend.service.PdfService;
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

@RestController
@RequestMapping("/atividades")
@Tag(name = "Atividade")
@CrossOrigin(origins = "*")
public class AtividadeController {

    @Autowired
    private AtividadeService atividadeService;

    @Autowired
    private PdfService pdfService;

    @PostMapping("/gerar")
    @Operation(summary = "Gerar uma nova atividade (prova) com questões selecionadas e embaralhadas")
    public ResponseEntity<AtividadeResponseDTO> gerarAtividade(
            @Valid @RequestBody AtividadeRequestDTO dto,
            @RequestParam Long professorId) {
        Atividade atividade = atividadeService.gerarAtividade(dto, professorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AtividadeResponseDTO(atividade));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Exportar atividade para PDF")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Long id) throws IOException {
        Atividade atividade = atividadeService.buscarPorId(id);
        byte[] pdf = pdfService.gerarPdfAtividade(atividade);
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
}