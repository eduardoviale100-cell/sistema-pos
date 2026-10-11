package com.example.pos.domain.model;

import com.example.pos.domain.exception.TipoMovimientoInventarioInvalidoException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Modelo de dominio puro para el registro de auditoría y movimientos de Kardex.
 */
public class MovimientoInventarioDomain {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of("ENTRADA", "SALIDA", "AJUSTE");

    private Long id;
    private Long productoId;
    private String productoNombre;
    private String tipoMovimiento; // "ENTRADA", "SALIDA", "AJUSTE"
    private Integer cantidad;
    private LocalDateTime fecha;
    private Long usuarioId;
    private String usuarioNombre;
    private String referencia;
    private String observaciones;

    public MovimientoInventarioDomain() {
        this.fecha = LocalDateTime.now();
    }

    public MovimientoInventarioDomain(Long productoId, String tipoMovimiento, Integer cantidad,
                                      Long usuarioId, String referencia, String observaciones) {
        this(null, productoId, null, tipoMovimiento, cantidad, LocalDateTime.now(), usuarioId, null, referencia, observaciones);
    }

    public MovimientoInventarioDomain(Long id, Long productoId, String productoNombre, String tipoMovimiento,
                                      Integer cantidad, LocalDateTime fecha, Long usuarioId, String usuarioNombre,
                                      String referencia, String observaciones) {
        validarParametros(productoId, tipoMovimiento, cantidad);
        this.id = id;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.tipoMovimiento = tipoMovimiento.trim().toUpperCase();
        this.cantidad = cantidad;
        this.fecha = (fecha != null) ? fecha : LocalDateTime.now();
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.referencia = referencia;
        this.observaciones = observaciones;
    }

    private void validarParametros(Long productoId, String tipo, Integer cantidad) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }
        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo.trim().toUpperCase())) {
            throw new TipoMovimientoInventarioInvalidoException(tipo);
        }
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser estrictamente mayor a cero.");
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

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

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

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovimientoInventarioDomain that = (MovimientoInventarioDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
