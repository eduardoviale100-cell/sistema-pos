package com.example.pos.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio pura para Método de Pago.
 *
 * <p>Representa un medio de cobro en las transacciones del punto de venta
 * (Efectivo, Tarjeta, Yape, Plin, Transferencia, etc.).</p>
 */
public class MetodoPagoDomain {

    private Long id;
    private String nombre;
    private Boolean activo;

    public MetodoPagoDomain() {
        this.activo = true;
    }

    public MetodoPagoDomain(String nombre) {
        this(null, nombre, true);
    }

    public MetodoPagoDomain(String nombre, Boolean activo) {
        this(null, nombre, activo);
    }

    public MetodoPagoDomain(Long id, String nombre, Boolean activo) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre.trim();
        this.activo = activo != null ? activo : true;
    }

    // =========================================================================
    // Lógica de Negocio
    // =========================================================================

    /**
     * Actualiza los datos del método de pago respetando invariantes.
     */
    public void actualizarDatos(String nuevoNombre, Boolean nuevoActivo) {
        validarNombre(nuevoNombre);
        this.nombre = nuevoNombre.trim();
        if (nuevoActivo != null) {
            this.activo = nuevoActivo;
        }
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public boolean isActivo() {
        return Boolean.TRUE.equals(this.activo);
    }

    // =========================================================================
    // Invariantes
    // =========================================================================

    private void validarNombre(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del método de pago es obligatorio y no puede estar vacío.");
        }
        if (valor.trim().length() < 2) {
            throw new IllegalArgumentException("El nombre del método de pago debe tener al menos 2 caracteres.");
        }
        if (valor.trim().length() > 50) {
            throw new IllegalArgumentException("El nombre del método de pago no puede exceder los 50 caracteres.");
        }
    }

    // =========================================================================
    // Getters y Setters
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre.trim();
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo != null ? activo : true;
    }

    // =========================================================================
    // Identidad
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetodoPagoDomain that)) return false;
        return Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return "MetodoPagoDomain{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                '}';
    }
}
