package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando una venta no existe en el sistema.
 */
public class VentaNoEncontradaException extends RuntimeException {

    public VentaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public static VentaNoEncontradaException porId(Long id) {
        return new VentaNoEncontradaException(
            String.format("No se encontró la venta con ID: %d", id)
        );
    }

    public static VentaNoEncontradaException porCodigo(String codigoFactura) {
        return new VentaNoEncontradaException(
            String.format("No se encontró la venta con código de factura: %s", codigoFactura)
        );
    }
}
