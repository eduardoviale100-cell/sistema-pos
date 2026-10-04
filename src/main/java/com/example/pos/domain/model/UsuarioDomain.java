package com.example.pos.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad de dominio pura para Usuario del sistema POS.
 *
 * <p>Encapsula las reglas de negocio y los invariantes de un usuario:
 * nombre completo, nombre de usuario único, contraseña (almacenada como hash),
 * rol de acceso y estado activo/inactivo.</p>
 *
 * <p><b>Nota:</b> Esta clase NO gestiona el hash de la contraseña directamente.
 * El encriptado se delega a un servicio de aplicación que utiliza BCrypt u otro
 * mecanismo. El dominio solo verifica que la contraseña no esté en blanco.</p>
 */
public class UsuarioDomain {

    private Long id;
    private String nombre;
    private String username;
    private String passwordHash;
    private RolUsuario rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    // =========================================================================
    // Constructores
    // =========================================================================

    public UsuarioDomain() {
        this.activo = true;
        this.rol = RolUsuario.VENDEDOR;
    }

    /**
     * Constructor para crear un nuevo usuario con invariantes validados.
     */
    public UsuarioDomain(String nombre, String username, String passwordHash, RolUsuario rol) {
        this(null, nombre, username, passwordHash, rol, true, null, null);
    }

    /**
     * Constructor completo para reconstrucción desde persistencia.
     */
    public UsuarioDomain(Long id, String nombre, String username, String passwordHash,
                         RolUsuario rol, Boolean activo,
                         LocalDateTime fechaCreacion, LocalDateTime fechaModificacion) {
        validarNombre(nombre);
        validarUsername(username);
        validarPassword(passwordHash);
        this.id = id;
        this.nombre = nombre.trim();
        this.username = username.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.rol = rol != null ? rol : RolUsuario.VENDEDOR;
        this.activo = activo != null ? activo : true;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
    }

    // =========================================================================
    // Lógica de Negocio
    // =========================================================================

    /**
     * Actualiza los datos del perfil del usuario.
     * La contraseña se actualiza solo si se proporciona un valor no nulo/vacío.
     */
    public void actualizarPerfil(String nuevoNombre, String nuevoUsername,
                                  String nuevoPasswordHash, RolUsuario nuevoRol) {
        validarNombre(nuevoNombre);
        validarUsername(nuevoUsername);
        this.nombre = nuevoNombre.trim();
        this.username = nuevoUsername.trim().toLowerCase();
        if (nuevoPasswordHash != null && !nuevoPasswordHash.isBlank()) {
            this.passwordHash = nuevoPasswordHash;
        }
        if (nuevoRol != null) {
            this.rol = nuevoRol;
        }
    }

    /** Activa al usuario para que pueda iniciar sesión. */
    public void activar() {
        this.activo = true;
    }

    /** Desactiva al usuario impidiendo su acceso al sistema. */
    public void desactivar() {
        this.activo = false;
    }

    /** @return {@code true} si el usuario puede iniciar sesión. */
    public boolean isActivo() {
        return Boolean.TRUE.equals(this.activo);
    }

    /** @return {@code true} si el usuario tiene rol ADMIN. */
    public boolean esAdmin() {
        return RolUsuario.ADMIN.equals(this.rol);
    }

    // =========================================================================
    // Invariantes
    // =========================================================================

    private void validarNombre(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El nombre completo del usuario es obligatorio.");
        }
        if (valor.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre completo debe tener al menos 3 caracteres.");
        }
        if (valor.trim().length() > 120) {
            throw new IllegalArgumentException("El nombre completo no puede exceder los 120 caracteres.");
        }
    }

    private void validarUsername(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (valor.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 3 caracteres.");
        }
        if (valor.trim().length() > 50) {
            throw new IllegalArgumentException("El nombre de usuario no puede exceder los 50 caracteres.");
        }
        if (!valor.trim().matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(
                    "El nombre de usuario solo puede contener letras, números, puntos, guiones y guiones bajos.");
        }
    }

    private void validarPassword(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("La contraseña del usuario es obligatoria.");
        }
    }

    // =========================================================================
    // Getters y Setters
    // =========================================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre.trim();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        validarUsername(username);
        this.username = username.trim().toLowerCase();
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) {
        validarPassword(passwordHash);
        this.passwordHash = passwordHash;
    }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) {
        this.rol = rol != null ? rol : RolUsuario.VENDEDOR;
    }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) {
        this.activo = activo != null ? activo : true;
    }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }

    // =========================================================================
    // Identidad
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioDomain that)) return false;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "UsuarioDomain{id=" + id + ", username='" + username + "', rol=" + rol + ", activo=" + activo + '}';
    }
}
