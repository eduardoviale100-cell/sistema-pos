package com.example.pos.infrastructure.input.rest.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de inicio de sesión exitoso.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String token;
    @Builder.Default
    private String tipo = "Bearer";

    private Long id;
    private String nombre;
    private String usuario;
    private String rol;

    private UsuarioInfo usuarioInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioInfo {
        private Long id;
        private String nombre;
        private String usuario;
        private String rol;
    }
}
