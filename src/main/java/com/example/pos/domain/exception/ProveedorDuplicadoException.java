package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta registrar un proveedor
 * con un RUC/DNI ya existente en el sistema.
 */
public class ProveedorDuplicadoException extends RuntimeException {

    public ProveedorDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public ProveedorDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static ProveedorDuplicadoException porDocumento(String rucDni) {
        return new ProveedorDuplicadoException("Ya existe un proveedor registrado con el RUC/DNI: " + rucDni);
    }
}
