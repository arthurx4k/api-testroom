package com.cefet.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.cefet.backend.dto.AutenticacaoRequestDTO;
import com.cefet.backend.dto.AutenticacaoResponseDTO;
import com.cefet.backend.service.AutenticacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Tag(name = "Autenticação")
@CrossOrigin(origins= "(*)")
public class AutenticacaoController {

     @Autowired
     private AutenticacaoService autenticacaoService;

     @PostMapping("/login")
     @Operation(summary = "Autenticar usuário")
     public ResponseEntity<AutenticacaoResponseDTO> autenticar(@Valid @RequestBody AutenticacaoRequestDTO autenticacaoRequestDTO) {
          AutenticacaoResponseDTO autenticacaoResponseDTO = autenticacaoService.autenticar(autenticacaoRequestDTO);
          return ResponseEntity.ok(autenticacaoResponseDTO);
     }
     
}
