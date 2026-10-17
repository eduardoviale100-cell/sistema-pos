package com.example.pos.infrastructure.input.rest.auth;

import com.example.pos.domain.exception.CredencialesInvalidasException;
import com.example.pos.domain.exception.UsuarioInactivoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador de excepciones para el módulo de Autenticación.
 */
@RestControllerAdvice(assignableTypes = AuthRestController.class)
public class AuthRestExceptionHandler {

    @ExceptionHandler({CredencialesInvalidasException.class, BadCredentialsException.class})
    public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Credenciales Inválidas",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.UNAUTHORIZED.value()
        ));
    }

    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioInactivo(UsuarioInactivoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "Usuario Inactivo",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.FORBIDDEN.value()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Errores de validación",
                "detalles", errores,
                "status", HttpStatus.BAD_REQUEST.value()
        ));
    }
}
