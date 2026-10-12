package com.example.pos.infrastructure.input.rest.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar o actualizar una categorÃ­a vÃ­a API REST.
 */
public class CategoriaRequestDto {

    @NotBlank(message = "El nombre de la categorÃ­a es obligatorio.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripciÃ³n no debe exceder los 1000 caracteres.")
    private String descripcion;

    public CategoriaRequestDto() {
    }

    public CategoriaRequestDto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

