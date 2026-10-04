package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando un usuario no es encontrado en el sistema.
 */
public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public UsuarioNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public static UsuarioNoEncontradoException porId(Long id) {
        return new UsuarioNoEncontradoException("No se encontró el usuario con ID: " + id);
    }

    public static UsuarioNoEncontradoException porUsername(String username) {
        return new UsuarioNoEncontradoException("No se encontró el usuario con nombre de usuario: " + username);
    }
}
