package com.example.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CompraRequestDto {

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Integer proveedorId;

    private Integer usuarioId;

    @NotEmpty(message = "La compra debe incluir al menos un producto")
    @Valid
    private List<CompraDetalleRequestDto> detalles;

    private String observaciones;
}
