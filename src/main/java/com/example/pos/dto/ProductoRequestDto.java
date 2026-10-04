package com.example.pos.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoRequestDto {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre del producto debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Pattern(regexp = "^$|^[A-Za-z0-9_-]{4,50}$", message = "El código de barras / SKU debe tener al menos 4 caracteres alfanuméricos")
    private String codigoBarras;

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de compra debe ser estrictamente mayor a 0 (mínimo 0.01)")
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser estrictamente mayor a 0 (mínimo 0.01)")
    private BigDecimal precioVenta;

    private Integer categoriaId;
    private Integer marcaId;
    private Integer proveedorId;

    public String getNombre() {
        return nombre != null ? nombre.trim() : null;
    }

    public String getCodigoBarras() {
        return (codigoBarras != null && !codigoBarras.isBlank()) ? codigoBarras.trim() : null;
    }
}
