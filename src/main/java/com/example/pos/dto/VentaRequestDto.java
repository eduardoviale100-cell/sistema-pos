package com.example.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VentaRequestDto {

    private Integer clienteId; // Opcional (venta al público en general)

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer usuarioId;

    @NotNull(message = "El ID de la sesión de caja es obligatorio")
    private Integer sesionCajaId;

    @NotEmpty(message = "La venta debe contener al menos un producto")
    @Valid
    private List<VentaDetalleRequestDto> detalles;

    @NotEmpty(message = "La venta debe registrar al menos un pago")
    @Valid
    private List<PagoRequestDto> pagos;

    private String observaciones;
}
