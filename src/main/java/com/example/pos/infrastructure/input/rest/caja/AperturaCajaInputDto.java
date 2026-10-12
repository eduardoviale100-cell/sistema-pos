package com.example.pos.infrastructure.input.rest.caja;

import com.example.pos.application.usecase.caja.*;
import com.example.pos.domain.model.caja.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AperturaCajaInputDto {

    @NotNull(message = "El ID de la caja es obligatorio")
    private Long cajaId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El monto inicial es obligatorio")
    @PositiveOrZero(message = "El monto inicial no puede ser negativo")
    private BigDecimal montoInicial;
}
