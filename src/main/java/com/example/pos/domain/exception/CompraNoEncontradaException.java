package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando una compra no es encontrada en el sistema.
 */
public class CompraNoEncontradaException extends RuntimeException {

    public CompraNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public static CompraNoEncontradaException porId(Long id) {
        return new CompraNoEncontradaException(
            String.format("No se encontró la compra con ID: %d", id)
        );
    }
}
