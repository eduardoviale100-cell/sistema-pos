package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando el stock actual es insuficiente para realizar una salida de inventario.
 */
public class StockInsuficienteInventarioException extends RuntimeException {

    public StockInsuficienteInventarioException(String productoNombre, Integer stockDisponible, Integer stockRequerido) {
        super(String.format("Stock insuficiente para '%s'. Stock disponible: %d, cantidad requerida: %d",
                productoNombre != null ? productoNombre : "Producto", stockDisponible, stockRequerido));
    }

    public StockInsuficienteInventarioException(Integer stockDisponible, Integer stockRequerido) {
        super(String.format("Stock insuficiente. Stock disponible: %d, cantidad requerida: %d",
                stockDisponible, stockRequerido));
    }

    public StockInsuficienteInventarioException(String mensaje) {
        super(mensaje);
    }
}
