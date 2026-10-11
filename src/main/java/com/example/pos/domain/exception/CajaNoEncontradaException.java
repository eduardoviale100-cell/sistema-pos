package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando una caja no existe en el sistema.
 */
public class CajaNoEncontradaException extends RuntimeException {

    public CajaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public static CajaNoEncontradaException porId(Long id) {
        return new CajaNoEncontradaException("No se encontró la caja con ID: " + id);
    }
}
