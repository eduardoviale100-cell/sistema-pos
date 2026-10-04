package com.example.pos.application.usecase;

/**
 * Comando inmutable para actualizar los datos de un usuario existente.
 *
 * <p>Si {@code passwordPlano} es nulo o vacío, la contraseña no se modifica.</p>
 */
public record ActualizarUsuarioCommand(
        String nombre,
        String username,
        String passwordPlano,
        String rol
) {
    public ActualizarUsuarioCommand {
        nombre = nombre != null ? nombre.trim() : null;
        username = username != null ? username.trim().toLowerCase() : null;
        rol = rol != null ? rol.toUpperCase().trim() : null;
    }
}
