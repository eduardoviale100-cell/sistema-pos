package com.example.pos.domain.exception;

import java.math.BigDecimal;

/**
 * Excepción de dominio lanzada cuando un egreso o retiro supera el saldo disponible en caja.
 */
public class SaldoInsuficienteCajaException extends RuntimeException {

    public SaldoInsuficienteCajaException(BigDecimal saldoActual, BigDecimal montoSolicitado) {
        super(String.format("Saldo insuficiente en caja para retiro. Saldo disponible: %s, Monto solicitado: %s",
                saldoActual, montoSolicitado));
    }
}
