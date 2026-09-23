package com.example.pos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AperturaCajaDto {

    @NotNull(message = "El ID de la caja es obligatorio")
    private Integer cajaId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer usuarioId;

    @NotNull(message = "El monto inicial es obligatorio")
    @PositiveOrZero(message = "El monto inicial no puede ser negativo")
    private BigDecimal montoInicial;
}
