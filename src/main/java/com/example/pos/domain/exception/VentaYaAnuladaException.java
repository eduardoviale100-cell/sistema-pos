package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta anular una venta que ya está anulada.
 */
public class VentaYaAnuladaException extends RuntimeException {

    public VentaYaAnuladaException(Long id) {
        super(String.format("La venta con ID %d ya se encuentra anulada.", id));
    }

    public VentaYaAnuladaException(String mensaje) {
        super(mensaje);
    }
}
