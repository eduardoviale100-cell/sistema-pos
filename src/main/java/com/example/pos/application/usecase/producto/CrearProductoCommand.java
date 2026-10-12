package com.example.pos.application.usecase.producto;

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
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        Long categoriaId,
        Long marcaId,
        Long proveedorId
) {
    public CrearProductoCommand {
        codigo      = codigo      != null ? codigo.trim()      : null;
        nombre      = nombre      != null ? nombre.trim()      : null;
        descripcion = descripcion != null ? descripcion.trim() : null;
    }

    public CrearProductoCommand(String codigo, String nombre, String descripcion, BigDecimal precioVenta) {
        this(codigo, nombre, descripcion, BigDecimal.ZERO, precioVenta, null, null, null);
    }
}
