package com.example.pos.infrastructure.input.rest;

import com.example.pos.domain.exception.InventarioNoEncontradoException;
import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.exception.StockInsuficienteInventarioException;
import com.example.pos.domain.exception.TipoMovimientoInventarioInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el adaptador REST del módulo Inventario.
 */
@RestControllerAdvice(assignableTypes = InventarioRestController.class)
public class InventarioRestExceptionHandler {

    @ExceptionHandler(InventarioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleInventarioNoEncontrado(InventarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Inventario No Encontrado",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleProductoNoEncontrado(ProductoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Producto No Encontrado",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler(StockInsuficienteInventarioException.class)
    public ResponseEntity<Map<String, Object>> handleStockInsuficiente(StockInsuficienteInventarioException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Stock Insuficiente",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler(TipoMovimientoInventarioInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleTipoMovimientoInvalido(TipoMovimientoInventarioInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Operación Inválida",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
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
