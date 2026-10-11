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

    @com.fasterxml.jackson.annotation.JsonAlias({"precio_compra"})
    @PositiveOrZero(message = "El precio de compra debe ser mayor o igual a cero.")
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio.")
    @PositiveOrZero(message = "El precio de venta debe ser mayor o igual a cero.")
    private BigDecimal precioVenta;

    @com.fasterxml.jackson.annotation.JsonAlias({"categoria_id"})
    private Long categoriaId;

    @com.fasterxml.jackson.annotation.JsonAlias({"marca_id"})
    private Long marcaId;

    @com.fasterxml.jackson.annotation.JsonAlias({"proveedor_id"})
    private Long proveedorId;

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

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(Long marcaId) {
        this.marcaId = marcaId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
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
