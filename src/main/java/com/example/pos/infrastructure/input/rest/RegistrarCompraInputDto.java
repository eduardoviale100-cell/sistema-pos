package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.RegistrarCompraCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para registrar una nueva compra a proveedor a través de la API REST v2.
 */
@Data
public class RegistrarCompraInputDto {

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long proveedorId;

    private Long usuarioId;

    private String observaciones;

    @NotEmpty(message = "La compra debe incluir al menos un producto en el detalle")
    @Valid
    private List<DetalleCompraInputDto> detalles;

    @Data
    public static class DetalleCompraInputDto {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        private Integer cantidad;

        @NotNull(message = "El precio unitario de compra es obligatorio")
        @Positive(message = "El precio unitario debe ser mayor a cero")
        private BigDecimal precioUnitario;
    }

    public RegistrarCompraCommand toCommand() {
        List<RegistrarCompraCommand.DetalleCompraCommand> detalleCommands = this.detalles != null
                ? this.detalles.stream()
                .map(d -> new RegistrarCompraCommand.DetalleCompraCommand(d.getProductoId(), d.getCantidad(), d.getPrecioUnitario()))
                .toList()
                : List.of();

        return new RegistrarCompraCommand(
                this.proveedorId,
                this.usuarioId,
                this.observaciones,
                detalleCommands
        );
    }
}
