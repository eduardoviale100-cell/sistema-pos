package com.example.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MetodoPagoRequestDto {

    @NotBlank(message = "El nombre del método de pago es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    private Boolean activo = true;
}
