package com.example.pos.infrastructure.input.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearCajaInputDto {

    @NotBlank(message = "El nombre de la caja es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre de la caja debe tener entre 2 y 50 caracteres")
    private String nombre;
}
