package com.example.pos.infrastructure.input.rest.metodopago;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear/actualizar un mÃ©todo de pago.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoRequestDto {

    @NotBlank(message = "El nombre del mÃ©todo de pago es obligatorio.")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres.")
    private String nombre;

    private Boolean activo;
}

