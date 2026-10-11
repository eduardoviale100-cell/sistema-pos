package com.example.pos.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Agregado raíz del dominio para Compras / Abastecimiento a Proveedores.
 */
public class CompraDomain {

    private Long id;
    private Long proveedorId;
    private String proveedorNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String observaciones;
    private List<CompraDetalleDomain> detalles = new ArrayList<>();

    public CompraDomain() {
        this.fecha = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public static CompraDomain crear(Long proveedorId, Long usuarioId, String observaciones) {
        if (proveedorId == null) {
            throw new IllegalArgumentException("El proveedor es obligatorio para registrar una compra.");
        }

        CompraDomain compra = new CompraDomain();
        compra.setProveedorId(proveedorId);
        compra.setUsuarioId(usuarioId);
        compra.setObservaciones(observaciones);
        compra.setFecha(LocalDateTime.now());
        return compra;
    }

    public void agregarDetalle(CompraDetalleDomain detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo.");
        }
        this.detalles.add(detalle);
        recalcularTotal();
    }

    public void recalcularTotal() {
        this.total = this.detalles.stream()
                .map(CompraDetalleDomain::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public ProveedorSummary getProveedor() {
        if (proveedorNombre != null || proveedorId != null) {
            return new ProveedorSummary(proveedorId, proveedorNombre);
        }
        return null;
    }

    public UsuarioSummary getUsuario() {
        if (usuarioNombre != null || usuarioId != null) {
            return new UsuarioSummary(usuarioId, usuarioNombre);
        }
        return null;
    }

    public record ProveedorSummary(Long id, String nombre) {}
    public record UsuarioSummary(Long id, String nombre) {}

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<CompraDetalleDomain> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<CompraDetalleDomain> detalles) {
        this.detalles = detalles != null ? new ArrayList<>(detalles) : new ArrayList<>();
        recalcularTotal();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompraDomain that = (CompraDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
