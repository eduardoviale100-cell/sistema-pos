package com.example.pos.application.usecase;

/**
 * Comando inmutable para actualizar una categoría existente.
 */
public record ActualizarCategoriaCommand(
        String nombre,
        String descripcion
) {
    public ActualizarCategoriaCommand {
        nombre      = nombre != null ? nombre.trim() : null;
        descripcion = descripcion != null ? descripcion.trim() : null;
    }
}
