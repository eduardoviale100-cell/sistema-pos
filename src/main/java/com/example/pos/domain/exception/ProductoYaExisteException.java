package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta registrar un producto
 * cuyo código (código de barras o código interno) ya existe en el sistema.
 *
 * <p>Equivalente semántico de {@link ClienteDuplicadoException} para el módulo de Productos.
 * Garantiza la unicidad del código como clave de negocio del producto.</p>
 */
public class ProductoYaExisteException extends RuntimeException {

    public ProductoYaExisteException(String mensaje) {
        super(mensaje);
    }

    public ProductoYaExisteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    // --- Métodos de fábrica semánticos ---

    /**
     * Crea la excepción cuando ya existe un producto con el mismo código.
     *
     * @param codigo Código duplicado detectado.
     * @return Instancia con mensaje descriptivo.
     */
    public static ProductoYaExisteException porCodigo(String codigo) {
        return new ProductoYaExisteException(
                "Ya existe un producto registrado con el código: " + codigo);
    }
}
