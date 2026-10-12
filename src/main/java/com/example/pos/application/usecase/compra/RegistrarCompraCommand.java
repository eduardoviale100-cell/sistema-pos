package com.example.pos.application.usecase.compra;

import java.math.BigDecimal;
import java.util.List;

/**
 * Command agnóstico para registrar una nueva compra a proveedor.
 */
public record RegistrarCompraCommand(
        Long proveedorId,
        Long usuarioId,
        String observaciones,
        List<DetalleCompraCommand> detalles
) {
    public record DetalleCompraCommand(
            Long productoId,
            Integer cantidad,
            BigDecimal precioUnitario
    ) {}
}
