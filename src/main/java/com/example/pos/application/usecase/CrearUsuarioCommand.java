package com.example.pos.application.usecase;

/**
 * Comando inmutable para registrar un nuevo usuario en el sistema.
 *
 * <p>La contraseña se recibe en texto plano; el caso de uso se encarga
 * de delegar el encriptado antes de persistir.</p>
 */
public record CrearUsuarioCommand(
        String nombre,
        String username,
        String passwordPlano,
        String rol
) {
    public CrearUsuarioCommand {
        nombre = nombre != null ? nombre.trim() : null;
        username = username != null ? username.trim().toLowerCase() : null;
        rol = rol != null ? rol.toUpperCase().trim() : "VENDEDOR";
    }
}
