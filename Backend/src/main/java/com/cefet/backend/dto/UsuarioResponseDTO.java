package com.cefet.backend.dto;

import com.cefet.backend.entity.CargoUsuario;
import com.cefet.backend.entity.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String email;
    private CargoUsuario cargo;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.cargo = usuario.getCargo();
    }
}