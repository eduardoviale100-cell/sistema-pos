package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando no se encuentra registro de inventario para un producto.
 */
public class InventarioNoEncontradoException extends RuntimeException {

    public InventarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public static InventarioNoEncontradoException porProductoId(Long productoId) {
        return new InventarioNoEncontradoException(
            String.format("No se encontró inventario para el producto con ID: %d", productoId)
        );
    }

    public static InventarioNoEncontradoException porId(Long id) {
        return new InventarioNoEncontradoException(
            String.format("No se encontró el registro de inventario con ID: %d", id)
        );
    }
}
