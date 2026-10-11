package com.example.pos.infrastructure.input.rest;

import com.example.pos.domain.exception.CompraNoEncontradaException;
import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.exception.ProveedorNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el adaptador REST del módulo Compras.
 */
@RestControllerAdvice(assignableTypes = CompraRestController.class)
public class CompraRestExceptionHandler {

    @ExceptionHandler(CompraNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleCompraNoEncontrada(CompraNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Compra No Encontrada",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler({
            ProveedorNoEncontradoException.class,
            ProductoNoEncontradoException.class
    })
    public ResponseEntity<Map<String, Object>> handleEntidadNoEncontrada(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Recurso No Encontrado",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
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
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Error de Validación",
                "detalles", errores,
                "status", HttpStatus.BAD_REQUEST.value()
        ));
    }
}
