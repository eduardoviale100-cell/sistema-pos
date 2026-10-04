package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando un producto no es encontrado en el sistema.
 * Pertenece estrictamente a la capa de dominio, libre de dependencias de frameworks.
 */
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public ProductoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    // --- Métodos de fábrica semánticos ---

    /**
     * Crea la excepción cuando la búsqueda falla por identificador primario.
     *
     * @param id Identificador del producto no encontrado.
     * @return Instancia con mensaje descriptivo.
     */
    public static ProductoNoEncontradoException porId(Long id) {
        return new ProductoNoEncontradoException(
                "No se encontró el producto con ID: " + id);
    }

    /**
     * Crea la excepción cuando la búsqueda falla por código de barras o código interno.
     *
     * @param codigo Código del producto no encontrado.
     * @return Instancia con mensaje descriptivo.
     */
    public static ProductoNoEncontradoException porCodigo(String codigo) {
        return new ProductoNoEncontradoException(
                "No se encontró el producto con código: " + codigo);
    }
}
