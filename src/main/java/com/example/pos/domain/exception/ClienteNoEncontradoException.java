package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando un cliente no es encontrado en el sistema.
 */
public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public ClienteNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static ClienteNoEncontradoException porId(Long id) {
        return new ClienteNoEncontradoException("No se encontró el cliente con ID: " + id);
    }

    public static ClienteNoEncontradoException porDocumento(String rucDni) {
        return new ClienteNoEncontradoException("No se encontró el cliente con RUC/DNI: " + rucDni);
    }
}
