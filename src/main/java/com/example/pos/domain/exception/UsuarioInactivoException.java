package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando un usuario inactivo intenta autenticarse u operar.
 */
public class UsuarioInactivoException extends RuntimeException {

    public UsuarioInactivoException(String username) {
        super("El usuario '" + username + "' se encuentra inactivo. Contacte al administrador.");
    }
}
