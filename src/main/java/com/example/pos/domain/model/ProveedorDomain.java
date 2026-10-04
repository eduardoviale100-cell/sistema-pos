package com.example.pos.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Entidad de dominio pura para Proveedor.
 *
 * <p>Representa a un proveedor de mercancías o servicios en el sistema POS.
 * Totalmente desacoplada de dependencias tecnológicas o de persistencia.</p>
 */
public class ProveedorDomain {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private Long id;
    private String nombre;
    private String rucDni;
    private String telefono;
    private String email;
    private String direccion;

    public ProveedorDomain() {
    }

    public ProveedorDomain(String nombre, String rucDni, String telefono, String email, String direccion) {
        this(null, nombre, rucDni, telefono, email, direccion);
    }

    public ProveedorDomain(Long id, String nombre, String rucDni, String telefono, String email, String direccion) {
        validarNombre(nombre);
        validarRucDni(rucDni);
        validarEmail(email);

        this.id = id;
        this.nombre = nombre.trim();
        this.rucDni = rucDni.trim();
        this.telefono = telefono != null ? telefono.trim() : null;
        this.email = email != null ? email.trim() : null;
        this.direccion = direccion != null ? direccion.trim() : null;
    }

    // =========================================================================
    // Lógica de Dominio
    // =========================================================================

    /**
     * Actualiza la información modificable del proveedor respetando sus invariantes.
     * El RUC/DNI es una clave de identidad fiscal y de negocio inmutable una vez creado.
     */
    public void actualizarDatos(String nuevoNombre, String nuevoTelefono, String nuevoEmail, String nuevaDireccion) {
        validarNombre(nuevoNombre);
        validarEmail(nuevoEmail);

        this.nombre = nuevoNombre.trim();
        this.telefono = nuevoTelefono != null ? nuevoTelefono.trim() : null;
        this.email = nuevoEmail != null ? nuevoEmail.trim() : null;
        this.direccion = nuevaDireccion != null ? nuevaDireccion.trim() : null;
    }

    // =========================================================================
    // Invariantes
    // =========================================================================

    private void validarNombre(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("La razón social o nombre del proveedor no puede estar vacía.");
        }
        if (valor.trim().length() < 3) {
            throw new IllegalArgumentException("La razón social debe tener al menos 3 caracteres.");
        }
        if (valor.trim().length() > 120) {
            throw new IllegalArgumentException("La razón social no puede exceder los 120 caracteres.");
        }
    }

    private void validarRucDni(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUC/DNI del proveedor es obligatorio.");
        }
        if (valor.trim().length() > 20) {
            throw new IllegalArgumentException("El RUC/DNI no puede exceder los 20 caracteres.");
        }
    }

    private void validarEmail(String valor) {
        if (valor != null && !valor.trim().isEmpty()) {
            String emailLimpio = valor.trim();
            if (emailLimpio.length() > 100) {
                throw new IllegalArgumentException("El correo electrónico no puede exceder los 100 caracteres.");
            }
            if (!EMAIL_PATTERN.matcher(emailLimpio).matches()) {
                throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
            }
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

    public String getRucDni() {
        return rucDni;
    }

    public void setRucDni(String rucDni) {
        validarRucDni(rucDni);
        this.rucDni = rucDni.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono != null ? telefono.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validarEmail(email);
        this.email = email != null ? email.trim() : null;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion != null ? direccion.trim() : null;
    }

    // =========================================================================
    // Identidad
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProveedorDomain that)) return false;
        return Objects.equals(rucDni, that.rucDni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rucDni);
    }

    @Override
    public String toString() {
        return "ProveedorDomain{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rucDni='" + rucDni + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
