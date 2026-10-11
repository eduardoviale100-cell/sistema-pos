package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta registrar un tipo de movimiento no permitido en el kardex.
 */
public class TipoMovimientoInventarioInvalidoException extends RuntimeException {

    public TipoMovimientoInventarioInvalidoException(String tipo) {
        super(String.format("Tipo de operación de inventario no válida: '%s'. Tipos admitidos: ENTRADA, SALIDA, AJUSTE.", tipo));
    }
}
