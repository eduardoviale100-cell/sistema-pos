package com.example.pos.infrastructure.input.rest.caja;

import com.example.pos.domain.model.caja.SesionCajaDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que expone el estado integral de la caja física y su sesión activa actual si existe.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoActualCajaDto {
    private Long cajaId;
    private String nombreCaja;
    private String estadoCaja; // "ABIERTA" o "CERRADA"
    private SesionCajaDomain sesionActiva;
    private ResumenTurnoCajaDto resumenTurno;
}
