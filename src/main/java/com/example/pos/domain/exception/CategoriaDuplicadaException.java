package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta crear o actualizar una categoría
 * con un nombre que ya existe en el sistema.
 */
public class CategoriaDuplicadaException extends RuntimeException {

    public CategoriaDuplicadaException(String mensaje) {
        super(mensaje);
    }

    public CategoriaDuplicadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static CategoriaDuplicadaException porNombre(String nombre) {
        return new CategoriaDuplicadaException("Ya existe una categoría registrada con el nombre: " + nombre);
    }
}
