package com.example.pos.infrastructure.input.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar o actualizar una categoría vía API REST.
 */
public class CategoriaRequestDto {

    @NotBlank(message = "El nombre de la categoría es obligatorio.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción no debe exceder los 1000 caracteres.")
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
