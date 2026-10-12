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
public class CierreCajaInputDto {

    @NotNull(message = "El monto real contado es obligatorio")
    @PositiveOrZero(message = "El monto real no puede ser negativo")
    private BigDecimal montoReal;
}
