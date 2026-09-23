package com.example.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovimientoCajaRequestDto {

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(?i)(INGRESO|RETIRO|EGRESO)$", message = "El tipo debe ser INGRESO, RETIRO o EGRESO")
    private String tipoMovimiento;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer usuarioId;

    private String referencia;
    private String observaciones;
}
