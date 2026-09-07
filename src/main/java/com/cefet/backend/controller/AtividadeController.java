package com.cefet.backend.controller;

import com.cefet.backend.dto.AtividadeComQuestoesRequestDTO;
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

    @PostMapping("/criar-com-questoes")
    @Operation(summary = "Criar atividade com questões selecionadas manualmente e múltiplas versões")
    public ResponseEntity<List<AtividadeResponseDTO>> criarComQuestoes(
            @Valid @RequestBody AtividadeComQuestoesRequestDTO dto,
            @RequestParam Long professorId) {
        List<Atividade> versoes = atividadeService.criarAtividadeComQuestoes(dto, professorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(versoes.stream().map(AtividadeResponseDTO::new).collect(Collectors.toList()));
    }
}