package com.cefet.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cefet.backend.dto.ProfessorRequestDTO;
import com.cefet.backend.dto.ProfessorResponseDTO;
import com.cefet.backend.entity.Professor;
import com.cefet.backend.service.ProfessorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/professores")
@Tag(name = "Professor")
@CrossOrigin(origins= "(*)")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @PostMapping
    @Operation(summary = "Criar um novo professor")
    public ResponseEntity<ProfessorResponseDTO> criar(@Valid @RequestBody ProfessorRequestDTO dto) {
        ProfessorResponseDTO response = professorService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os professores")
    public ResponseEntity<List<ProfessorResponseDTO>> listarTodos() {
        List<ProfessorResponseDTO> lista = professorService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar professor por ID")
    public ResponseEntity<ProfessorResponseDTO> buscarPorId(@PathVariable Long id) {
        ProfessorResponseDTO response = professorService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/perfil")
    @Operation(summary = "Obter perfil do professor logado")
    public ResponseEntity<ProfessorResponseDTO> obterPerfil(@RequestParam Long professorId) {
        ProfessorResponseDTO response = professorService.buscarPorId(professorId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um professor existente")
    public ResponseEntity<ProfessorResponseDTO> atualizar(@PathVariable Long id,
            @Valid @RequestBody ProfessorRequestDTO dto) {
        ProfessorResponseDTO response = professorService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/nome")
    public ResponseEntity<Map<String, String>> atualizarNome(@PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String nome = payload.get("nome");
        Professor professor = professorService.atualizarNome(id, nome);
        return ResponseEntity.ok(Map.of("nome", professor.getNome()));
    }

    @PutMapping("/{id}/foto")
    public ResponseEntity<Map<String, String>> atualizarFoto(@PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String fotoUrl = payload.get("fotoUrl");
        return ResponseEntity.ok(Map.of("foto", fotoUrl));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um professor")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        professorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}