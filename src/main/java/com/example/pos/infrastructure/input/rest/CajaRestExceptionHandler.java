package com.example.pos.infrastructure.input.rest;

import com.example.pos.domain.exception.CajaNoEncontradaException;
import com.example.pos.domain.exception.CajaYaAbiertaException;
import com.example.pos.domain.exception.SaldoInsuficienteCajaException;
import com.example.pos.domain.exception.SesionCajaNoAbiertaException;
import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para los adaptadores REST del módulo Caja.
 * Mapea excepciones de dominio a códigos de estado HTTP estándar.
 */
@RestControllerAdvice(assignableTypes = CajaRestController.class)
public class CajaRestExceptionHandler {

    @ExceptionHandler(CajaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleCajaNoEncontrada(CajaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Caja No Encontrada",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Usuario No Encontrado",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler(CajaYaAbiertaException.class)
    public ResponseEntity<Map<String, Object>> handleCajaYaAbierta(CajaYaAbiertaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Caja Ya Abierta",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler(SesionCajaNoAbiertaException.class)
    public ResponseEntity<Map<String, Object>> handleSesionCajaNoAbierta(SesionCajaNoAbiertaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Sesión de Caja Inactiva",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler(SaldoInsuficienteCajaException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(SaldoInsuficienteCajaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Saldo Insuficiente en Caja",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Estado de Caja Inválido",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Solicitud Inválida",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
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
