package com.example.pos.infrastructure.output.persistence.usuario;

import com.example.pos.domain.model.usuario.UsuarioDomain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA del modulo Usuario (capa de infraestructura).
 * Desacoplada del modelo de dominio UsuarioDomain.
 *
 * IMPORTANTE: La columna de contrasena se llama "contrasena" (sin tilde)
 * para garantizar compatibilidad de codificacion en todos los entornos.
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

    /** Nombre de usuario unico para inicio de sesion. */
    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Hash BCrypt de la contrasena del usuario.
     * Columna BD: "contrasena" (ASCII, sin tilde) para evitar PSQLException
     * por conflictos de codificacion UTF-8/cp1252 en distintos entornos.
     */
    @Column(name = "contrasena", nullable = false, length = 255)
    private String passwordHash;

    /** Rol del usuario en el sistema: ADMIN, VENDEDOR o CAJERO. */
    @Column(nullable = false, length = 30)
    private String rol;

    /** Indica si el usuario esta activo en el sistema. */
    @Column(nullable = false)
    private Boolean activo;

    /** Fecha de creacion del registro (se asigna automaticamente). */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha de la ultima modificacion del registro. */
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