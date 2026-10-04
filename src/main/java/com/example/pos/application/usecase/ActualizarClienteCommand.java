package com.example.pos.application.usecase;

/**
 * Comando inmutable que encapsula los datos de entrada necesarios
 * para ejecutar el caso de uso de actualizar un cliente existente.
 *
 * <p>No incluye el ID ni el RUC/DNI, ya que son campos de identidad
 * que no se permiten modificar una vez creado el cliente.
 * El ID se pasa directamente al método {@code ejecutar} del caso de uso.</p>
 */
public record ActualizarClienteCommand(
        String nombre,
        String telefono,
        String email,
        String direccion,
        Boolean estado
) {
    /**
     * Constructor compacto con normalización de cadenas.
     * Garantiza que los valores de texto lleguen sin espacios redundantes.
     */
    public ActualizarClienteCommand {
        nombre    = nombre    != null ? nombre.trim()    : null;
        telefono  = telefono  != null ? telefono.trim()  : null;
        email     = email     != null ? email.trim()     : null;
        direccion = direccion != null ? direccion.trim() : null;
        // 'estado' es Boolean, no requiere trim
    }
}
