package com.example.pos.infrastructure.input.rest.venta;

import com.example.pos.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el adaptador REST del modulo Ventas.
 */
@RestControllerAdvice(assignableTypes = VentaRestController.class)
public class VentaRestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(VentaRestExceptionHandler.class);

    @ExceptionHandler(VentaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleVentaNoEncontrada(VentaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Venta No Encontrada",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler(StockInsuficienteVentaException.class)
    public ResponseEntity<Map<String, Object>> handleStockInsuficiente(StockInsuficienteVentaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Stock Insuficiente",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler(MontoPagoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleMontoPagoInsuficiente(MontoPagoInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Pago Insuficiente",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
        ));
    }

    @ExceptionHandler(VentaYaAnuladaException.class)
    public ResponseEntity<Map<String, Object>> handleVentaYaAnulada(VentaYaAnuladaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Venta Ya Anulada",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler(SesionCajaNoAbiertaException.class)
    public ResponseEntity<Map<String, Object>> handleSesionCajaNoAbierta(SesionCajaNoAbiertaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Sesion de Caja Inactiva",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler({
            ProductoNoEncontradoException.class,
            ClienteNoEncontradoException.class,
            UsuarioNoEncontradoException.class,
            MetodoPagoNoEncontradoException.class
    })
    public ResponseEntity<Map<String, Object>> handleEntidadesNoEncontradas(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Recurso No Encontrado",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND.value()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Solicitud Invalida",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "Conflicto de Estado",
                "mensaje", ex.getMessage(),
                "status", HttpStatus.CONFLICT.value()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Error de Validacion",
                "detalles", errores,
                "status", HttpStatus.BAD_REQUEST.value()
        ));
    }

    /**
     * Catch-all: captura CUALQUIER excepcion o error no manejado por los handlers anteriores.
     * Registra la traza completa en los logs del servidor para diagnostico
     * y devuelve un cuerpo JSON estructurado en lugar de la pagina de error por defecto de Spring.
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleGenericThrowable(Throwable ex) {
        log.error(">>> ERROR NO CONTROLADO en modulo Ventas: {} - {}", ex.getClass().getName(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Error Interno del Servidor",
                "mensaje", ex.getMessage() != null ? ex.getMessage() : "Error inesperado al procesar la venta.",
                "tipo", ex.getClass().getSimpleName(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        ));
    }
}
