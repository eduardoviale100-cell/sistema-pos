package com.example.pos.infrastructure.input.rest.venta;

import com.example.pos.application.usecase.venta.RegistrarVentaCommand;




import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para registrar una nueva venta a travÃ©s de la API REST v2.
 */



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

    // No-args constructor required for Jackson
    public RegistrarVentaInputDto() {}

    // Getters and Setters
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getSesionCajaId() { return sesionCajaId; }
    public void setSesionCajaId(Long sesionCajaId) { this.sesionCajaId = sesionCajaId; }
    public List<DetalleItemInputDto> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleItemInputDto> detalles) { this.detalles = detalles; }
    public List<PagoItemInputDto> getPagos() { return pagos; }
    public void setPagos(List<PagoItemInputDto> pagos) { this.pagos = pagos; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    


    public static class DetalleItemInputDto {

        public DetalleItemInputDto() {}

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        private Integer cantidad;

        private BigDecimal precioUnitario;
    }

    


    public static class PagoItemInputDto {

        public PagoItemInputDto() {}

        public Long getMetodoPagoId() { return metodoPagoId; }
        public void setMetodoPagoId(Long metodoPagoId) { this.metodoPagoId = metodoPagoId; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
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

