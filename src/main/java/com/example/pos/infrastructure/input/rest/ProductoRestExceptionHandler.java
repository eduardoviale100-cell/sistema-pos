package com.example.pos.infrastructure.input.rest;

import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.exception.ProductoYaExisteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para los adaptadores REST del módulo Producto.
 *
 * <p>Traduce las excepciones de dominio y de validación de Bean Validation en
 * respuestas HTTP estandarizadas con un cuerpo JSON limpio y consistente.</p>
 *
 * <p>Jerarquía de respuestas manejadas:
 * <ul>
 *   <li>{@link ProductoNoEncontradoException} → 404 NOT FOUND</li>
 *   <li>{@link ProductoYaExisteException}     → 409 CONFLICT</li>
 *   <li>{@link IllegalArgumentException}      → 400 BAD REQUEST</li>
 *   <li>{@link MethodArgumentNotValidException}→ 400 BAD REQUEST (detalles por campo)</li>
 * </ul>
 * </p>
 *
 * <p>Al usar {@code basePackages}, este handler sólo intercepta excepciones lanzadas
 * desde controladores dentro del paquete REST de infraestructura.</p>
 */
@RestControllerAdvice(basePackages = "com.example.pos.infrastructure.input.rest")
public class ProductoRestExceptionHandler {

    // =========================================================================
    // Excepciones de Dominio — Producto
    // =========================================================================

    /**
     * Captura {@link ProductoNoEncontradoException} lanzada por los casos de uso
     * cuando no existe un producto con el ID o código solicitado.
     *
     * @return 404 NOT FOUND con cuerpo JSON descriptivo.
     */
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleProductoNoEncontrado(
            ProductoNoEncontradoException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error",   "Producto No Encontrado",
                "mensaje", ex.getMessage(),
                "status",  HttpStatus.NOT_FOUND.value()
        ));
    }

    /**
     * Captura {@link ProductoYaExisteException} lanzada por el caso de uso de creación
     * cuando el código del producto ya está registrado en el sistema.
     *
     * @return 409 CONFLICT con cuerpo JSON descriptivo.
     */
    @ExceptionHandler(ProductoYaExisteException.class)
    public ResponseEntity<Map<String, Object>> handleProductoYaExiste(
            ProductoYaExisteException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error",   "Producto Duplicado",
                "mensaje", ex.getMessage(),
                "status",  HttpStatus.CONFLICT.value()
        ));
    }

    // =========================================================================
    // Excepciones Genéricas de Validación
    // =========================================================================

    /**
     * Captura {@link IllegalArgumentException} lanzada por invariantes del dominio
     * (por ejemplo, precio negativo, nombre vacío, stock negativo).
     *
     * @return 400 BAD REQUEST con el mensaje de la invariante violada.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error",   "Solicitud Inválida",
                "mensaje", ex.getMessage(),
                "status",  HttpStatus.BAD_REQUEST.value()
        ));
    }

    /**
     * Captura los errores de Bean Validation lanzados por {@code @Valid} en el controller,
     * devolviendo un mapa detallado de errores por campo.
     *
     * @return 400 BAD REQUEST con el mapa de campos inválidos y sus mensajes.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> erroresCampos = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                erroresCampos.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error",    "Errores de validación",
                "detalles", erroresCampos,
                "status",   HttpStatus.BAD_REQUEST.value()
        ));
    }
}
