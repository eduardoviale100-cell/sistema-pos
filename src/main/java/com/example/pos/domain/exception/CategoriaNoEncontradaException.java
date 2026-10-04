package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando una categoría no es encontrada en el sistema.
 */
public class CategoriaNoEncontradaException extends RuntimeException {

    public CategoriaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public CategoriaNoEncontradaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static CategoriaNoEncontradaException porId(Long id) {
        return new CategoriaNoEncontradaException("No se encontró la categoría con ID: " + id);
    }

    public static CategoriaNoEncontradaException porNombre(String nombre) {
        return new CategoriaNoEncontradaException("No se encontró la categoría con nombre: " + nombre);
    }
}
