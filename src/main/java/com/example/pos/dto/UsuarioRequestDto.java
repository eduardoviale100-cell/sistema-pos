package com.example.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDto {

    @NotBlank(message = "El nombre del usuario es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    private String nombre;

    @NotBlank(message = "El nombre de usuario (login) es obligatorio")
    @Size(max = 50, message = "El usuario no puede superar los 50 caracteres")
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 255, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    private String rol = "VENDEDOR";

    private Boolean activo = true;
}
