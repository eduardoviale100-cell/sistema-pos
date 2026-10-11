package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.AjustarStockCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO de entrada para registrar un ajuste de inventario (ENTRADA, SALIDA o AJUSTE).
 */
@Data
public class AjustarStockInputDto {

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotBlank(message = "El tipo de operación es obligatorio (ENTRADA, SALIDA, AJUSTE)")
    @Pattern(regexp = "^(?i)(ENTRADA|SALIDA|AJUSTE)$", message = "El tipo debe ser ENTRADA, SALIDA o AJUSTE")
    private String tipo;

    private Long usuarioId;

    private String motivo;

    public AjustarStockCommand toCommand(Long productoId) {
        return new AjustarStockCommand(
                productoId,
                this.cantidad,
                this.tipo,
                this.usuarioId,
                this.motivo
        );
    }
}
