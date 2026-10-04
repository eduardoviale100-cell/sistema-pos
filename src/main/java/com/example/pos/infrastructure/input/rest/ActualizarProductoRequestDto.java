package com.example.pos.infrastructure.input.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO de entrada para actualizar un producto vía HTTP PUT.
 */
public class ActualizarProductoRequestDto {

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción no debe exceder los 1000 caracteres.")
    private String descripcion;

    @NotNull(message = "El precio de venta es obligatorio.")
    @PositiveOrZero(message = "El precio de venta debe ser mayor o igual a cero.")
    private BigDecimal precioVenta;

    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero.")
    private Integer stock;

    private Boolean estado;

    public ActualizarProductoRequestDto() {
    }

    public ActualizarProductoRequestDto(String nombre, String descripcion, BigDecimal precioVenta, Integer stock, Boolean estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.estado = estado;
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

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
