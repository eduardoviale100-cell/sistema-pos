package com.example.pos.application.usecase;

/**
 * Comando inmutable para actualizar la información de un proveedor existente.
 * El RUC/DNI no se incluye por ser la identidad fiscal única e inmutable.
 */
public record ActualizarProveedorCommand(
        String nombre,
        String telefono,
        String email,
        String direccion
) {
    public ActualizarProveedorCommand {
        nombre    = nombre != null ? nombre.trim() : null;
        telefono  = telefono != null ? telefono.trim() : null;
        email     = email != null ? email.trim() : null;
        direccion = direccion != null ? direccion.trim() : null;
    }
}
