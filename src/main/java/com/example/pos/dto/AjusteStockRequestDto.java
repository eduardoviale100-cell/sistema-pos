package com.example.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AjusteStockRequestDto {

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotBlank(message = "El tipo de operación es obligatorio (ENTRADA, SALIDA, AJUSTE)")
    @Pattern(regexp = "^(?i)(ENTRADA|SALIDA|AJUSTE)$", message = "El tipo debe ser ENTRADA, SALIDA o AJUSTE")
    private String tipo;

    private String motivo;
}
