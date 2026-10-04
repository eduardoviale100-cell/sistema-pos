package com.example.pos.infrastructure.input.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para actualizar la información de un proveedor vía API REST.
 * El RUC/DNI no se incluye porque la identidad fiscal es inmutable.
 */
public class ActualizarProveedorRequestDto {

    @NotBlank(message = "La razón social o nombre del proveedor es obligatorio.")
    @Size(min = 3, max = 120, message = "La razón social debe tener entre 3 y 120 caracteres.")
    private String nombre;

    @Pattern(regexp = "^$|^[0-9+() -]{6,20}$", message = "El teléfono contiene un formato inválido.")
    private String telefono;

    @Email(message = "Debe proporcionar un formato de correo electrónico válido.")
    @Size(max = 100, message = "El correo electrónico no debe exceder los 100 caracteres.")
    private String email;

    @Size(max = 255, message = "La dirección no debe exceder los 255 caracteres.")
    private String direccion;

    public ActualizarProveedorRequestDto() {
    }

    public ActualizarProveedorRequestDto(String nombre, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
}
