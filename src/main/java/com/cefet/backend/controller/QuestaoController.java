package com.cefet.backend.controller;

import com.cefet.backend.dto.QuestaoRequestDTO;
import com.cefet.backend.dto.QuestaoResponseDTO;
import com.cefet.backend.service.QuestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questoes")
@Tag(name = "Questão")
public class QuestaoController {

    @Autowired
    private QuestaoService questaoService;

    @PostMapping
    @Operation(summary = "Criar uma nova questão")
    public ResponseEntity<QuestaoResponseDTO> criar(
            @Valid @RequestBody QuestaoRequestDTO dto,
            @RequestParam Long professorId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questaoService.criar(dto, professorId));
    }

    @GetMapping
    @Operation(summary = "Listar questões de um professor")
    public ResponseEntity<List<QuestaoResponseDTO>> listarPorProfessor(
            @RequestParam Long professorId) {
        return ResponseEntity.ok(questaoService.listarPorProfessor(professorId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar questão por ID")
    public ResponseEntity<QuestaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(questaoService.buscarPorId(id));
    }

    @GetMapping("/por-categoria")
    @Operation(summary = "Listar questões de uma categoria")
    public ResponseEntity<List<QuestaoResponseDTO>> listarPorCategoria(
            @RequestParam Long categoriaId,
            @RequestParam Long professorId) {
        return ResponseEntity.ok(questaoService.listarPorCategoria(categoriaId, professorId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma questão")
    public ResponseEntity<QuestaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody QuestaoRequestDTO dto,
            @RequestParam Long professorId) {
        return ResponseEntity.ok(questaoService.atualizar(id, dto, professorId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma questão")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @RequestParam Long professorId) {
        questaoService.excluir(id, professorId);
        return ResponseEntity.noContent().build();
    }
}