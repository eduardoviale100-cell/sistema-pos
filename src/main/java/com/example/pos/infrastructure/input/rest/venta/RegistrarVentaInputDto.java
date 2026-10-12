package com.example.pos.infrastructure.input.rest.venta;

import com.example.pos.application.usecase.venta.RegistrarVentaCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para registrar una nueva venta a travÃ©s de la API REST v2.
 */
@Data
public class RegistrarVentaInputDto {

    private Long clienteId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID de la sesiÃ³n de caja es obligatorio")
    private Long sesionCajaId;

    @NotEmpty(message = "La venta debe contener al menos un producto en el detalle")
    @Valid
    private List<DetalleItemInputDto> detalles;

    @NotEmpty(message = "La venta debe registrar al menos un mÃ©todo de pago")
    @Valid
    private List<PagoItemInputDto> pagos;

    private String observaciones;

    @Data
    public static class DetalleItemInputDto {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        private Integer cantidad;

        private BigDecimal precioUnitario;
    }

    @Data
    public static class PagoItemInputDto {
        @NotNull(message = "El ID del mÃ©todo de pago es obligatorio")
        private Long metodoPagoId;

        @NotNull(message = "El monto del pago es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        private BigDecimal monto;
    }

    public RegistrarVentaCommand toCommand() {
        List<RegistrarVentaCommand.DetalleVentaCommand> detalleCommands = this.detalles != null
                ? this.detalles.stream()
                .map(d -> new RegistrarVentaCommand.DetalleVentaCommand(d.getProductoId(), d.getCantidad(), d.getPrecioUnitario()))
                .toList()
                : List.of();

        List<RegistrarVentaCommand.PagoVentaCommand> pagoCommands = this.pagos != null
                ? this.pagos.stream()
                .map(p -> new RegistrarVentaCommand.PagoVentaCommand(p.getMetodoPagoId(), p.getMonto()))
                .toList()
                : List.of();

        return new RegistrarVentaCommand(
                this.sesionCajaId,
                this.usuarioId,
                this.clienteId,
                this.observaciones,
                detalleCommands,
                pagoCommands
        );
    }
}

