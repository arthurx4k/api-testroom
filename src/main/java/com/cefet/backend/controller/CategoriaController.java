package com.cefet.backend.controller;

import com.cefet.backend.dto.CategoriaRequestDTO;
import com.cefet.backend.dto.CategoriaResponseDTO;
import com.cefet.backend.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@Tag(name = "Categoria")
@CrossOrigin(origins= "(*)")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    @Operation(summary = "Criar uma nova categoria")
    public ResponseEntity<CategoriaResponseDTO> criar(
            @Valid @RequestBody CategoriaRequestDTO dto,
            @RequestParam Long professorId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.inserir(dto, professorId));
    }

    @GetMapping
    @Operation(summary = "Listar categorias acessíveis (próprias + compartilhadas)")
    public ResponseEntity<List<CategoriaResponseDTO>> listarAcessiveis(
            @RequestParam Long professorId) {
        return ResponseEntity.ok(categoriaService.listarAcessiveis(professorId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma categoria")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto,
            @RequestParam Long professorId) {
        return ResponseEntity.ok(categoriaService.atualizar(id, dto, professorId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma categoria")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @RequestParam Long professorId) {
        categoriaService.excluir(id, professorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{categoriaId}/compartilhar/{professorAlvoId}")
    @Operation(summary = "Compartilhar categoria com outro professor")
    public ResponseEntity<Void> compartilhar(
            @PathVariable Long categoriaId,
            @PathVariable Long professorAlvoId,
            @RequestParam Long professorOrigemId) {
        categoriaService.compartilhar(categoriaId, professorAlvoId, professorOrigemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{categoriaId}/compartilhar/{professorAlvoId}")
    @Operation(summary = "Remover compartilhamento de categoria")
    public ResponseEntity<Void> descompartilhar(
            @PathVariable Long categoriaId,
            @PathVariable Long professorAlvoId,
            @RequestParam Long professorOrigemId) {
        categoriaService.descompartilhar(categoriaId, professorAlvoId, professorOrigemId);
        return ResponseEntity.noContent().build();
    }
}