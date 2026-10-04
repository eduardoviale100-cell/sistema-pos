package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta registrar un usuario
 * con un nombre de usuario (username) que ya existe en el sistema.
 */
public class UsuarioDuplicadoException extends RuntimeException {

    public UsuarioDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public UsuarioDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static UsuarioDuplicadoException porUsername(String username) {
        return new UsuarioDuplicadoException(
                "Ya existe un usuario registrado con el nombre de usuario: " + username);
    }
}
