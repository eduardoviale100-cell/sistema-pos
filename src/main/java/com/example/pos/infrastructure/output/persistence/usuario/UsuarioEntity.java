package com.example.pos.infrastructure.output.persistence.usuario;


import com.example.pos.domain.model.usuario.UsuarioDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA del mÃ³dulo Usuario (capa de infraestructura).
 * Desacoplada del modelo de dominio {@link com.example.pos.domain.model.usuario.UsuarioDomain}.
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del usuario. */
    @Column(nullable = false, length = 120)
    private String nombre;

    /** Nombre de usuario Ãºnico para inicio de sesiÃ³n. */
    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    private String username;

    /** ContraseÃ±a almacenada como hash (BCrypt). */
    @Column(name = "contraseÃ±a", nullable = false, length = 255)
    private String passwordHash;

    /** Rol del usuario en el sistema (ADMIN, VENDEDOR, CAJERO). */
    @Column(nullable = false, length = 30)
    private String rol;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (rol == null) {
            rol = "VENDEDOR";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}

