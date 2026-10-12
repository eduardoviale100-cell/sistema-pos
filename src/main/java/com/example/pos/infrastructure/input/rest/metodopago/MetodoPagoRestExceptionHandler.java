package com.example.pos.infrastructure.input.rest.metodopago;

import com.example.pos.domain.exception.MetodoPagoDuplicadoException;
import com.example.pos.domain.exception.MetodoPagoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para los adaptadores REST del mÃ³dulo MetodoPago.
 */
@RestControllerAdvice(assignableTypes = MetodoPagoRestController.class)
public class MetodoPagoRestExceptionHandler {

    @ExceptionHandler(MetodoPagoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(MetodoPagoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "MÃ©todo de Pago No Encontrado",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler(MetodoPagoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicado(MetodoPagoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "MÃ©todo de Pago Duplicado",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Solicitud InvÃ¡lida",
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
                "error", "Errores de validaciÃ³n",
                "detalles", errores,
                "status", HttpStatus.BAD_REQUEST.value()
        ));
    }
}

