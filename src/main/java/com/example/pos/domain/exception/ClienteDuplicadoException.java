package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta registrar un cliente que ya existe
 * (por ejemplo, con el mismo RUC/DNI o correo electrónico único).
 */
public class ClienteDuplicadoException extends RuntimeException {

    public ClienteDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public ClienteDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static ClienteDuplicadoException porDocumento(String rucDni) {
        return new ClienteDuplicadoException("Ya existe un cliente registrado con el RUC/DNI: " + rucDni);
    }

    public static ClienteDuplicadoException porEmail(String email) {
        return new ClienteDuplicadoException("Ya existe un cliente registrado con el correo electrónico: " + email);
    }
}
