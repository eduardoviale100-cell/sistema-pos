package com.example.pos.application.usecase;

/**
 * Comando inmutable para actualizar un método de pago existente.
 */
public record ActualizarMetodoPagoCommand(
        String nombre,
        Boolean activo
) {
    public ActualizarMetodoPagoCommand {
        nombre = nombre != null ? nombre.trim() : null;
    }
}
