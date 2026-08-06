package com.cefet.backend.dto;

import com.cefet.backend.entity.CargoUsuario;
import com.cefet.backend.entity.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AutenticacaoResponseDTO {
    private Long id;
    private String email;
    private CargoUsuario cargo;
    private Long professorId;
    private String professorNome;
    private String foto;

    public AutenticacaoResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.cargo = usuario.getCargo();
        
    }
}