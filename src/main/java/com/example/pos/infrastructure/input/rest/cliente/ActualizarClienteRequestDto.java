package com.example.pos.infrastructure.input.rest.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la actualizaciÃ³n parcial o total de un cliente vÃ­a HTTP PUT.
 *
 * <p>A diferencia de {@link ClienteRequestDto}, este DTO no incluye el campo
 * {@code rucDni} (RUC/DNI), ya que el documento de identidad es un campo de
 * identidad inmutable una vez creado el cliente.</p>
 *
 * <p>El campo {@code estado} permite activar o desactivar el cliente directamente
 * desde una operaciÃ³n de actualizaciÃ³n sin necesidad de un endpoint separado.</p>
 */
public class ActualizarClienteRequestDto {

    @NotBlank(message = "El nombre o razÃ³n social del cliente es obligatorio.")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres.")
    private String nombre;

    @com.fasterxml.jackson.annotation.JsonAlias({"documento", "ruc_dni"})
    private String rucDni;

    @Pattern(regexp = "^$|^9\\d{8}$", message = "El telÃ©fono debe ser un celular vÃ¡lido de 9 dÃ­gitos que comience con 9.")
    private String telefono;

    @Email(message = "El formato del correo electrÃ³nico es invÃ¡lido.")
    @Size(max = 100, message = "El correo no debe exceder los 100 caracteres.")
    private String email;

    @Size(max = 255, message = "La direcciÃ³n no debe exceder los 255 caracteres.")
    private String direccion;

    /**
     * Permite cambiar el estado activo/inactivo del cliente.
     * Si es {@code null}, el estado actual del cliente no se modifica.
     */
    private Boolean estado;

    public ActualizarClienteRequestDto() {
    }

    public ActualizarClienteRequestDto(String nombre, String telefono, String email,
                                       String direccion, Boolean estado) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.estado = estado;
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}

