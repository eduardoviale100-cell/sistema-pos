package com.example.pos.infrastructure.input.rest;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la creación o recepción de datos de un cliente vía HTTP REST.
 * Soporta deserialización desde el frontend tanto con 'rucDni' como con 'documento'.
 */
public class ClienteRequestDto {

    @NotBlank(message = "El nombre o razón social del cliente es obligatorio.")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres.")
    private String nombre;

    @NotBlank(message = "El RUC o DNI es obligatorio.")
    @Pattern(regexp = "^(\\d{8}|(10|20)\\d{9})$", message = "El documento debe ser un DNI de 8 dígitos o un RUC de 11 dígitos válido.")
    @JsonAlias({"documento", "ruc_dni"})
    private String rucDni;

    @Pattern(regexp = "^$|^9\\d{8}$", message = "El teléfono debe ser un celular válido de 9 dígitos que comience con 9.")
    private String telefono;

    @Email(message = "El formato del correo electrónico es inválido.")
    @Size(max = 100, message = "El correo no debe exceder los 100 caracteres.")
    private String email;

    @Size(max = 255, message = "La dirección no debe exceder los 255 caracteres.")
    private String direccion;

    public ClienteRequestDto() {
    }

    public ClienteRequestDto(String nombre, String rucDni, String telefono, String email, String direccion) {
        this.nombre = nombre;
        this.rucDni = rucDni;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    // --- Getters y Setters ---

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

    // Compatibilidad adicional con llamadas frontend que usan setDocumento/getDocumento
    public String getDocumento() {
        return rucDni;
    }

    public void setDocumento(String documento) {
        this.rucDni = documento;
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
