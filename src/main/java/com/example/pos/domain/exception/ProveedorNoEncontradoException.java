package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando un proveedor no es encontrado en el sistema.
 */
public class ProveedorNoEncontradoException extends RuntimeException {

    public ProveedorNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public ProveedorNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static ProveedorNoEncontradoException porId(Long id) {
        return new ProveedorNoEncontradoException("No se encontró el proveedor con ID: " + id);
    }

    public static ProveedorNoEncontradoException porDocumento(String rucDni) {
        return new ProveedorNoEncontradoException("No se encontró el proveedor con RUC/DNI: " + rucDni);
    }
}
