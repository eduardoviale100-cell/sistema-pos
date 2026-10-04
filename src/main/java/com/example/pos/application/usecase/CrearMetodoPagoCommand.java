package com.example.pos.application.usecase;

/**
 * Comando inmutable para registrar un nuevo método de pago.
 */
public record CrearMetodoPagoCommand(
        String nombre,
        Boolean activo
) {
    public CrearMetodoPagoCommand {
        nombre = nombre != null ? nombre.trim() : null;
        activo = activo != null ? activo : true;
    }
}
