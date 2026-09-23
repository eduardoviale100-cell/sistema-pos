package com.example.pos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CierreCajaDto {

    @NotNull(message = "El monto real contado es obligatorio")
    @PositiveOrZero(message = "El monto real no puede ser negativo")
    private BigDecimal montoReal;
}
