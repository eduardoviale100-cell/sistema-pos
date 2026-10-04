package com.example.pos.application.usecase;

/**
 * Comando inmutable para actualizar una marca existente.
 */
public record ActualizarMarcaCommand(
        String nombre,
        String descripcion
) {
    public ActualizarMarcaCommand {
        nombre      = nombre != null ? nombre.trim() : null;
        descripcion = descripcion != null ? descripcion.trim() : null;
    }
}
