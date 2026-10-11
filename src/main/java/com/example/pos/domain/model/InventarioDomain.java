package com.example.pos.domain.model;

import com.example.pos.domain.exception.StockInsuficienteInventarioException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de dominio puro para el control de Inventario y existencias físicas.
 */
public class InventarioDomain {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private String productoCodigo;
    private Integer stockActual;
    private Integer stockMinimo;
    private LocalDateTime ultimaActualizacion;

    // Objeto anidado para serialización hacia el frontend
    private ProductoDomain producto;

    public InventarioDomain() {
        this.stockActual = 0;
        this.stockMinimo = 5;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public InventarioDomain(Long productoId, String productoNombre, String productoCodigo, Integer stockActual, Integer stockMinimo) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoCodigo = productoCodigo;
        this.stockActual = (stockActual != null && stockActual >= 0) ? stockActual : 0;
        this.stockMinimo = (stockMinimo != null && stockMinimo >= 0) ? stockMinimo : 5;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public InventarioDomain(Long id, Long productoId, String productoNombre, String productoCodigo,
                            Integer stockActual, Integer stockMinimo, LocalDateTime ultimaActualizacion) {
        this.id = id;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoCodigo = productoCodigo;
        this.stockActual = (stockActual != null) ? stockActual : 0;
        this.stockMinimo = (stockMinimo != null) ? stockMinimo : 5;
        this.ultimaActualizacion = (ultimaActualizacion != null) ? ultimaActualizacion : LocalDateTime.now();
    }

    // Reglas de negocio
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a ingresar debe ser mayor a cero.");
        }
        this.stockActual += cantidad;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public void disminuirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a retirar debe ser mayor a cero.");
        }
        if (this.stockActual < cantidad) {
            throw new StockInsuficienteInventarioException(this.productoNombre, this.stockActual, cantidad);
        }
        this.stockActual -= cantidad;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public void establecerStock(int nuevaCantidad) {
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        this.stockActual = nuevaCantidad;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public boolean isBajoStock() {
        return this.stockActual != null && this.stockMinimo != null && this.stockActual <= this.stockMinimo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getProductoCodigo() {
        return productoCodigo;
    }

    public void setProductoCodigo(String productoCodigo) {
        this.productoCodigo = productoCodigo;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public ProductoDomain getProducto() {
        return producto;
    }

    public void setProducto(ProductoDomain producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventarioDomain that = (InventarioDomain) o;
        return Objects.equals(id, that.id) || Objects.equals(productoId, that.productoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productoId);
    }
}
