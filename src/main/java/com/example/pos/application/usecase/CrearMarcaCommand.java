package com.example.pos.application.usecase;

/**
 * Comando inmutable con los datos necesarios para registrar una nueva marca.
 */
public record CrearMarcaCommand(
        String nombre,
        String descripcion
) {
    public CrearMarcaCommand {
        nombre      = nombre != null ? nombre.trim() : null;
        descripcion = descripcion != null ? descripcion.trim() : null;
    }
}
