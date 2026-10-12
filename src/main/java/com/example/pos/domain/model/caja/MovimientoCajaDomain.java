package com.example.pos.domain.model.caja;

import com.example.pos.domain.model.usuario.UsuarioDomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de dominio puro para Movimientos (Ingresos / Retiros) de Caja.
 */
public class MovimientoCajaDomain {

    private Long id;
    private SesionCajaDomain sesionCaja;
    private UsuarioDomain usuario;
    private String tipoMovimiento; // "INGRESO", "RETIRO"
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String referencia;
    private String observaciones;

    public MovimientoCajaDomain() {
        this.fecha = LocalDateTime.now();
    }

    public MovimientoCajaDomain(Long id, SesionCajaDomain sesionCaja, UsuarioDomain usuario,
                                String tipoMovimiento, BigDecimal monto, LocalDateTime fecha,
                                String referencia, String observaciones) {
        validarParametros(sesionCaja, tipoMovimiento, monto);
        this.id = id;
        this.sesionCaja = sesionCaja;
        this.usuario = usuario;
        this.tipoMovimiento = normalizarTipo(tipoMovimiento);
        this.monto = monto;
        this.fecha = (fecha != null) ? fecha : LocalDateTime.now();
        this.referencia = referencia;
        this.observaciones = observaciones;
    }

    private void validarParametros(SesionCajaDomain sesion, String tipo, BigDecimal monto) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesión de caja es obligatoria.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser estrictamente positivo.");
        }
    }

    private String normalizarTipo(String tipo) {
        String t = tipo.trim().toUpperCase();
        if ("EGRESO".equals(t)) {
            return "RETIRO";
        }
        if (!"INGRESO".equals(t) && !"RETIRO".equals(t)) {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + tipo + ". Debe ser INGRESO o RETIRO.");
        }
        return t;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SesionCajaDomain getSesionCaja() {
        return sesionCaja;
    }

    public void setSesionCaja(SesionCajaDomain sesionCaja) {
        this.sesionCaja = sesionCaja;
    }

    public UsuarioDomain getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDomain usuario) {
        this.usuario = usuario;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = normalizarTipo(tipoMovimiento);
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
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
        MovimientoCajaDomain that = (MovimientoCajaDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
