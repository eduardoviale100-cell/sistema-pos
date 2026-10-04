package com.example.pos.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Entidad de dominio pura para Cliente.
 * Representa el modelo del negocio sin acoplamiento a frameworks (Spring, JPA, Jakarta).
 */
public class ClienteDomain {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private Long id;
    private String nombre;
    private String rucDni;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean estado;

    public ClienteDomain() {
        this.estado = true;
    }

    /**
     * Constructor para la creación de un nuevo cliente en el dominio (sin ID persistido).
     */
    public ClienteDomain(String nombre, String rucDni, String telefono, String email, String direccion) {
        this(null, nombre, rucDni, telefono, email, direccion, true);
    }

    /**
     * Constructor completo (para reconstitución desde la capa de persistencia).
     */
    public ClienteDomain(Long id, String nombre, String rucDni, String telefono, String email, String direccion, Boolean estado) {
        validarNombre(nombre);
        validarRucDni(rucDni);
        validarEmail(email);

        this.id = id;
        this.nombre = nombre.trim();
        this.rucDni = rucDni.trim();
        this.telefono = telefono != null ? telefono.trim() : null;
        this.email = email != null ? email.trim() : null;
        this.direccion = direccion != null ? direccion.trim() : null;
        this.estado = estado != null ? estado : true;
    }

    // --- Métodos de Lógica de Dominio ---

    /**
     * Actualiza la información de contacto y nombre del cliente validando invariantes.
     */
    public void actualizarDatos(String nuevoNombre, String nuevoTelefono, String nuevoEmail, String nuevaDireccion) {
        validarNombre(nuevoNombre);
        validarEmail(nuevoEmail);

        this.nombre = nuevoNombre.trim();
        this.telefono = nuevoTelefono != null ? nuevoTelefono.trim() : null;
        this.email = nuevoEmail != null ? nuevoEmail.trim() : null;
        this.direccion = nuevaDireccion != null ? nuevaDireccion.trim() : null;
    }

    public void activar() {
        this.estado = true;
    }

    public void desactivar() {
        this.estado = false;
    }

    public boolean isActivo() {
        return Boolean.TRUE.equals(this.estado);
    }

    // --- Validaciones de Invariantes ---

    private void validarNombre(String valorNombre) {
        if (valorNombre == null || valorNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede ser nulo ni estar vacío.");
        }
        if (valorNombre.trim().length() > 120) {
            throw new IllegalArgumentException("El nombre del cliente no puede exceder los 120 caracteres.");
        }
    }

    private void validarRucDni(String valorRucDni) {
        if (valorRucDni == null || valorRucDni.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUC/DNI del cliente es obligatorio.");
        }
        String doc = valorRucDni.trim();
        if (doc.length() < 8 || doc.length() > 20) {
            throw new IllegalArgumentException("El RUC/DNI debe tener entre 8 y 20 caracteres.");
        }
    }

    private void validarEmail(String valorEmail) {
        if (valorEmail != null && !valorEmail.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(valorEmail.trim()).matches()) {
                throw new IllegalArgumentException("El formato del correo electrónico es inválido: " + valorEmail);
            }
        }
    }

    // --- Getters y Setters ---

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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado != null ? estado : true;
    }

    // --- Métodos de Identidad y Representación ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClienteDomain that)) return false;
        return Objects.equals(rucDni, that.rucDni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rucDni);
    }

    @Override
    public String toString() {
        return "ClienteDomain{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rucDni='" + rucDni + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", direccion='" + direccion + '\'' +
                ", estado=" + estado +
                '}';
    }
}
