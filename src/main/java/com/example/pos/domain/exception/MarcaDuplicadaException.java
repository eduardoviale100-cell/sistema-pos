package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta crear o actualizar una marca
 * con un nombre que ya existe en el sistema.
 */
public class MarcaDuplicadaException extends RuntimeException {

    public MarcaDuplicadaException(String mensaje) {
        super(mensaje);
    }

    public MarcaDuplicadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static MarcaDuplicadaException porNombre(String nombre) {
        return new MarcaDuplicadaException("Ya existe una marca registrada con el nombre: " + nombre);
    }
}
