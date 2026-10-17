package com.example.pos.infrastructure.input.rest.usuario;

import com.example.pos.domain.model.usuario.UsuarioDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de salida para exponer la información de un usuario sin revelar el hash de la contraseña.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDto {

    private Long id;
    private String nombre;
    private String username;
    private String usuario;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public static UsuarioResponseDto fromDomain(UsuarioDomain domain) {
        if (domain == null) {
            return null;
        }
        return UsuarioResponseDto.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .username(domain.getUsername())
                .usuario(domain.getUsername())
                .rol(domain.getRol() != null ? domain.getRol().name() : "VENDEDOR")
                .activo(domain.isActivo())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaModificacion(domain.getFechaModificacion())
                .build();
    }
}
