package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta registrar un método de pago
 * con un nombre que ya existe en el sistema.
 */
public class MetodoPagoDuplicadoException extends RuntimeException {

    public MetodoPagoDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public MetodoPagoDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static MetodoPagoDuplicadoException porNombre(String nombre) {
        return new MetodoPagoDuplicadoException("Ya existe un método de pago registrado con el nombre: " + nombre);
    }
}
