package com.example.pos.application.usecase;

import java.math.BigDecimal;

/**
 * Comando inmutable que encapsula los datos necesarios para actualizar
 * un producto existente en el catálogo.
 *
 * <p>El código de barras / código interno es inmutable, por lo que no forma
 * parte de este comando.</p>
 */
public record ActualizarProductoCommand(
        String nombre,
        String descripcion,
        BigDecimal precioVenta,
        Integer stock,
        Boolean estado
) {
    /**
     * Constructor compacto para sanitizar cadenas de texto.
     */
    public ActualizarProductoCommand {
        nombre      = nombre != null ? nombre.trim() : null;
        descripcion = descripcion != null ? descripcion.trim() : null;
    }
}
