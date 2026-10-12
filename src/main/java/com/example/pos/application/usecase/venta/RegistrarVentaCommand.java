package com.example.pos.application.usecase.venta;

import java.math.BigDecimal;
import java.util.List;

/**
 * Command agnóstico para registrar una venta en el POS.
 */
public record RegistrarVentaCommand(
        Long sesionCajaId,
        Long usuarioId,
        Long clienteId,
        String observaciones,
        List<DetalleVentaCommand> detalles,
        List<PagoVentaCommand> pagos
) {
    public record DetalleVentaCommand(
            Long productoId,
            Integer cantidad,
            BigDecimal precioUnitario
    ) {}

    public record PagoVentaCommand(
            Long metodoPagoId,
            BigDecimal monto
    ) {}
}
