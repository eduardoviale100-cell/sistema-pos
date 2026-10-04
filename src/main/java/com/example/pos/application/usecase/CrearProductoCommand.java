package com.example.pos.application.usecase;

import java.math.BigDecimal;

/**
 * Comando inmutable que encapsula los datos de entrada necesarios
 * para ejecutar el caso de uso de registrar un nuevo producto.
 *
 * <p>Sigue el patrón Command para desacoplar completamente al adaptador de entrada
 * (REST Controller) del caso de uso. El record aplica normalización de cadenas en
 * su constructor compacto para garantizar datos limpios desde el borde del sistema.</p>
 */
public record CrearProductoCommand(
        String codigo,
        String nombre,
        String descripcion,
        BigDecimal precioVenta
) {
    /**
     * Constructor compacto con normalización defensiva de cadenas.
     * Garantiza que los valores de texto lleguen sin espacios redundantes al caso de uso.
     */
    public CrearProductoCommand {
        codigo      = codigo      != null ? codigo.trim()      : null;
        nombre      = nombre      != null ? nombre.trim()      : null;
        descripcion = descripcion != null ? descripcion.trim() : null;
        // precioVenta es BigDecimal, no requiere trim
    }
}
