package com.example.pos.domain.model.caja;

import java.util.Objects;

/**
 * Modelo de dominio puro para Caja física o punto de cobro.
 */
public class CajaDomain {

    private Long id;
    private String nombre;
    private String estado; // "ABIERTA", "CERRADA"

    public CajaDomain() {
        this.estado = "CERRADA";
    }

    public CajaDomain(String nombre) {
        this(null, nombre, "CERRADA");
    }

    public CajaDomain(Long id, String nombre, String estado) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre.trim();
        this.estado = (estado != null && !estado.isBlank()) ? estado.toUpperCase() : "CERRADA";
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la caja no puede estar vacío.");
        }
    }

    public void abrir() {
        if ("ABIERTA".equalsIgnoreCase(this.estado)) {
            throw new IllegalStateException("La caja ya se encuentra en estado ABIERTA.");
        }
        this.estado = "ABIERTA";
    }

    public void cerrar() {
        this.estado = "CERRADA";
    }

    public boolean isAbierta() {
        return "ABIERTA".equalsIgnoreCase(this.estado);
    }

    // Getters y Setters
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CajaDomain that = (CajaDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
