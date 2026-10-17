package com.example.pos.infrastructure.input.rest.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de inicio de sesión (Login).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @JsonProperty("usuario")
    @JsonAlias({"username", "user"})
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria.")
    @JsonProperty("contrasena")
    @JsonAlias({"password", "contraseña", "clave"})
    private String contrasena;
}
