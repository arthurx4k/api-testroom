package com.cefet.backend.controller;

import com.cefet.backend.dto.UsuarioRequestDTO;
import com.cefet.backend.dto.UsuarioResponseDTO;
import com.cefet.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuário")
@CrossOrigin(origins= "(*)")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Criar um novo usuário")
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}