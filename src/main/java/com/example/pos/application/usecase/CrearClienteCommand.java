package com.example.pos.application.usecase;

/**
 * Comando inmutable que encapsula los datos de entrada necesarios
 * para ejecutar el caso de uso de registrar un nuevo cliente.
 */
public record CrearClienteCommand(
        String nombre,
        String rucDni,
        String telefono,
        String email,
        String direccion
) {
    public CrearClienteCommand {
        nombre = nombre != null ? nombre.trim() : null;
        rucDni = rucDni != null ? rucDni.trim() : null;
        telefono = telefono != null ? telefono.trim() : null;
        email = email != null ? email.trim() : null;
        direccion = direccion != null ? direccion.trim() : null;
    }
}
