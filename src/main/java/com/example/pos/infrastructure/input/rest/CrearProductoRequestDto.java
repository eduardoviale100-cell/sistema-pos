package com.example.pos.infrastructure.input.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO de entrada para el registro de un nuevo producto vía HTTP POST.
 *
 * <p>Aplica Bean Validation (Jakarta) en el borde del sistema para garantizar
 * que los datos lleguen válidos a los casos de uso. Las reglas aquí son
 * defensivas (formato/presencia); las reglas de negocio (unicidad, invariantes)
 * permanecen en el dominio y los casos de uso.</p>
 *
 * <p>No incluye {@code stock} ni {@code estado} porque:
 * <ul>
 *   <li>{@code stock} se inicializa en 0 por defecto al crear un producto;
 *       se gestiona a través del módulo de Inventario.</li>
 *   <li>{@code estado} se inicializa en {@code true} (activo) por defecto.</li>
 * </ul>
 * </p>
 */
public class CrearProductoRequestDto {

    @NotBlank(message = "El código del producto es obligatorio.")
    @Size(max = 50, message = "El código no debe exceder los 50 caracteres.")
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción no debe exceder los 1000 caracteres.")
    private String descripcion;

    @NotNull(message = "El precio de venta es obligatorio.")
    @PositiveOrZero(message = "El precio de venta debe ser mayor o igual a cero.")
    private BigDecimal precioVenta;

    // =========================================================================
    // Constructores
    // =========================================================================

    public CrearProductoRequestDto() {
    }

    public CrearProductoRequestDto(String codigo, String nombre, String descripcion,
                                   BigDecimal precioVenta) {
        this.codigo      = codigo;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
    }

    // =========================================================================
    // Getters y Setters
    // =========================================================================

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
}
