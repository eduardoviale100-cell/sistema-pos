package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando el stock disponible no cubre la venta solicitada.
 */
public class StockInsuficienteVentaException extends RuntimeException {

    public StockInsuficienteVentaException(String productoNombre, Integer stockDisponible, Integer stockSolicitado) {
        super(String.format("Stock insuficiente para '%s'. Stock disponible: %d, solicitado: %d",
                productoNombre, stockDisponible, stockSolicitado));
    }

    public StockInsuficienteVentaException(String mensaje) {
        super(mensaje);
    }
}
