package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando un método de pago no es encontrado.
 */
public class MetodoPagoNoEncontradoException extends RuntimeException {

    public MetodoPagoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public MetodoPagoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static MetodoPagoNoEncontradoException porId(Long id) {
        return new MetodoPagoNoEncontradoException("No se encontró el método de pago con ID: " + id);
    }

    public static MetodoPagoNoEncontradoException porNombre(String nombre) {
        return new MetodoPagoNoEncontradoException("No se encontró el método de pago con nombre: " + nombre);
    }
}
