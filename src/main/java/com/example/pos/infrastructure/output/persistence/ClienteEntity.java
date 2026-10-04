package com.example.pos.infrastructure.output.persistence;

import jakarta.persistence.*;

/**
 * Entidad JPA para la persistencia de clientes en la base de datos relacional.
 * Representa la tabla física "clientes" en la capa de Infraestructura.
 */
@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "documento", unique = true, length = 20)
    private String rucDni;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "estado")
    private Boolean estado = true;

    public ClienteEntity() {
    }

    public ClienteEntity(Long id, String nombre, String rucDni, String telefono, String email, String direccion, Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.rucDni = rucDni;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.estado = estado != null ? estado : true;
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
        this.nombre = nombre;
    }

    public String getRucDni() {
        return rucDni;
    }

    public void setRucDni(String rucDni) {
        this.rucDni = rucDni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
