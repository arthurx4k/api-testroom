package com.cefet.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cefet.backend.dto.ProfessorRequestDTO;
import com.cefet.backend.dto.ProfessorResponseDTO;
import com.cefet.backend.service.ProfessorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/professores")
@Tag(name = "Professor")
@CrossOrigin(origins = "*")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @PostMapping
    @Operation(summary = "Criar um novo professor")
    public ResponseEntity<ProfessorResponseDTO> criar(@Valid @RequestBody ProfessorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(professorService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os professores")
    public ResponseEntity<List<ProfessorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(professorService.listarTodos());
    }

    @GetMapping("/perfil")
    @Operation(summary = "Obter perfil do professor logado")
    public ResponseEntity<ProfessorResponseDTO> obterPerfil(@RequestParam Long professorId) {
        return ResponseEntity.ok(professorService.buscarPorId(professorId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar professor por ID")
    public ResponseEntity<ProfessorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(professorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um professor existente")
    public ResponseEntity<ProfessorResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProfessorRequestDTO dto) {
        return ResponseEntity.ok(professorService.atualizar(id, dto));
    }

    @PutMapping("/{id}/nome")
    @Operation(summary = "Atualizar apenas o nome do professor")
    public ResponseEntity<ProfessorResponseDTO> atualizarNome(
            @PathVariable Long id,
            @RequestBody ProfessorRequestDTO dto) {
        return ResponseEntity.ok(professorService.atualizarNome(id, dto.getNome()));
    }

    @PutMapping("/{id}/foto")
    @Operation(summary = "Atualizar a foto de perfil do professor")
    public ResponseEntity<ProfessorResponseDTO> atualizarFoto(
            @PathVariable Long id,
            @RequestBody ProfessorRequestDTO dto) {
        return ResponseEntity.ok(professorService.atualizarFoto(id, dto.getFoto()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um professor")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        professorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}