package com.example.pos.application.usecase;

/**
 * Comando inmutable con los datos necesarios para registrar una nueva categoría.
 */
public record CrearCategoriaCommand(
        String nombre,
        String descripcion
) {
    public CrearCategoriaCommand {
        nombre      = nombre != null ? nombre.trim() : null;
        descripcion = descripcion != null ? descripcion.trim() : null;
    }
}
