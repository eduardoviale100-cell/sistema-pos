package com.example.pos.domain.exception;

import java.math.BigDecimal;

/**
 * Excepción de dominio lanzada cuando el monto abonado es menor al total de la venta.
 */
public class MontoPagoInsuficienteException extends RuntimeException {

    public MontoPagoInsuficienteException(BigDecimal totalVenta, BigDecimal totalPagado) {
        super(String.format("Monto de pago insuficiente. Total venta: %s, Total recibido: %s",
                totalVenta != null ? totalVenta.toString() : "0.00",
                totalPagado != null ? totalPagado.toString() : "0.00"));
    }

    public MontoPagoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
