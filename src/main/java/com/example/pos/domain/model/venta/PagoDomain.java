package com.example.pos.domain.model.venta;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa un pago registrado para una venta (Entidad interna del dominio).
 */
public class PagoDomain {

    private Long id;
    private Long metodoPagoId;
    private String metodoPagoNombre;
    private BigDecimal monto;
    private java.time.LocalDateTime fecha;

    public PagoDomain() {
        this.monto = BigDecimal.ZERO;
        this.fecha = java.time.LocalDateTime.now();
    }

    public PagoDomain(Long metodoPagoId, String metodoPagoNombre, BigDecimal monto) {
        this(null, metodoPagoId, metodoPagoNombre, monto, java.time.LocalDateTime.now());
    }

    public PagoDomain(Long id, Long metodoPagoId, String metodoPagoNombre, BigDecimal monto, java.time.LocalDateTime fecha) {
        if (metodoPagoId == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor a cero.");
        }
        this.id = id;
        this.metodoPagoId = metodoPagoId;
        this.metodoPagoNombre = metodoPagoNombre;
        this.monto = monto;
        this.fecha = (fecha != null) ? fecha : java.time.LocalDateTime.now();
    }

    public boolean esEfectivo() {
        return metodoPagoNombre != null && metodoPagoNombre.trim().toUpperCase().contains("EFECTIVO");
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMetodoPagoId() {
        return metodoPagoId;
    }

    public void setMetodoPagoId(Long metodoPagoId) {
        this.metodoPagoId = metodoPagoId;
    }

    public String getMetodoPagoNombre() {
        return metodoPagoNombre;
    }

    public void setMetodoPagoNombre(String metodoPagoNombre) {
        this.metodoPagoNombre = metodoPagoNombre;
    }

    public MetodoPagoSummary getMetodoPago() {
        if (metodoPagoNombre != null || metodoPagoId != null) {
            return new MetodoPagoSummary(metodoPagoId, metodoPagoNombre);
        }
        return null;
    }

    public record MetodoPagoSummary(Long id, String nombre) {}

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public java.time.LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(java.time.LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagoDomain that = (PagoDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
