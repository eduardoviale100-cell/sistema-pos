package com.example.pos.application.usecase.inventario;

/**
 * Command agnóstico para registrar un ajuste manual o movimiento en el inventario.
 */
public record AjustarStockCommand(
        Long productoId,
        Integer cantidad,
        String tipo,
        Long usuarioId,
        String motivo
) {}
