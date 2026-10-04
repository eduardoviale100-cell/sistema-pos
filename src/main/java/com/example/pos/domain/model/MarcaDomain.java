package com.example.pos.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio pura para Marca.
 *
 * <p>Representa una marca comercial de productos sin acoplamiento a frameworks
 * externos (Spring, JPA). Encapsula las invariantes y reglas de negocio.</p>
 */
public class MarcaDomain {

    private Long id;
    private String nombre;
    private String descripcion;

    public MarcaDomain() {
    }

    public MarcaDomain(String nombre, String descripcion) {
        this(null, nombre, descripcion);
    }

    public MarcaDomain(Long id, String nombre, String descripcion) {
        validarNombre(nombre);
        this.id = id;
        this.nombre = nombre.trim();
        this.descripcion = descripcion != null ? descripcion.trim() : null;
    }

    // =========================================================================
    // Métodos de Lógica de Negocio
    // =========================================================================

    /**
     * Actualiza la información de la marca respetando sus invariantes.
     *
     * @param nuevoNombre Nombre actualizado (no nulo, 2 a 100 caracteres).
     * @param nuevaDescripcion Descripción actualizada (opcional).
     */
    public void actualizarDatos(String nuevoNombre, String nuevaDescripcion) {
        validarNombre(nuevoNombre);
        this.nombre = nuevoNombre.trim();
        this.descripcion = nuevaDescripcion != null ? nuevaDescripcion.trim() : null;
    }

    // =========================================================================
    // Validaciones de Invariantes
    // =========================================================================

    private void validarNombre(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio y no puede estar vacío.");
        }
        if (valor.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre de la marca no puede exceder los 100 caracteres.");
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : null;
    }

    // =========================================================================
    // Identidad
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarcaDomain that)) return false;
        return Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return "MarcaDomain{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
