package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando las credenciales de autenticación son incorrectas.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Credenciales inválidas: usuario o contraseña incorrectos.");
    }

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
