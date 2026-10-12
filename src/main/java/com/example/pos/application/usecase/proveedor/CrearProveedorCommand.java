package com.example.pos.application.usecase.proveedor;

/**
 * Comando inmutable que contiene los datos necesarios para registrar un nuevo proveedor.
 */
public record CrearProveedorCommand(
        String nombre,
        String rucDni,
        String telefono,
        String email,
        String direccion
) {
    public CrearProveedorCommand {
        nombre    = nombre != null ? nombre.trim() : null;
        rucDni    = rucDni != null ? rucDni.trim() : null;
        telefono  = telefono != null ? telefono.trim() : null;
        email     = email != null ? email.trim() : null;
        direccion = direccion != null ? direccion.trim() : null;
    }
}
