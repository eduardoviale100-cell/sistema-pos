package com.example.pos.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Detalle individual de una línea de compra a proveedor.
 */
public class CompraDetalleDomain {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public CompraDetalleDomain() {
        this.cantidad = 1;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public CompraDetalleDomain(Long productoId, String productoNombre, Integer cantidad, BigDecimal precioUnitario) {
        validarParametros(productoId, cantidad, precioUnitario);
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    public CompraDetalleDomain(Long id, Long productoId, String productoNombre, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.id = id;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = (subtotal != null) ? subtotal : calcularSubtotal();
    }

    public BigDecimal calcularSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    private void validarParametros(Long productoId, Integer cantidad, BigDecimal precioUnitario) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
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

    public ProductoSummary getProducto() {
        if (productoNombre != null || productoId != null) {
            return new ProductoSummary(productoId, productoNombre);
        }
        return null;
    }

    public record ProductoSummary(Long id, String nombre) {}

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompraDetalleDomain that = (CompraDetalleDomain) o;
        return Objects.equals(id, that.id) && Objects.equals(productoId, that.productoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productoId);
    }
}
