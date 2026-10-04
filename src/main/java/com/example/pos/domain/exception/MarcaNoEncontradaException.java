package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando una marca no es encontrada en el sistema.
 */
public class MarcaNoEncontradaException extends RuntimeException {

    public MarcaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public MarcaNoEncontradaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static MarcaNoEncontradaException porId(Long id) {
        return new MarcaNoEncontradaException("No se encontró la marca con ID: " + id);
    }

    public static MarcaNoEncontradaException porNombre(String nombre) {
        return new MarcaNoEncontradaException("No se encontró la marca con nombre: " + nombre);
    }
}
