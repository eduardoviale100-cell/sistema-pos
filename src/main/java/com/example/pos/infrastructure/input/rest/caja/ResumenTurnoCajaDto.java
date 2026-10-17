package com.example.pos.infrastructure.input.rest.caja;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Resumen financiero y operacional consolidado de una sesión / turno de caja.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenTurnoCajaDto {
    private Long sesionId;
    private Long cajaId;
    private String nombreCaja;
    private Long usuarioId;
    private String nombreUsuario;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private String estado;
    private BigDecimal montoInicial;
    private BigDecimal totalVentasEfectivo;
    private BigDecimal totalVentasOtrosMetodos;
    private BigDecimal totalVentas;
    private int cantidadVentas;
    private BigDecimal totalOtrosIngresos;
    private BigDecimal totalOtrosEgresos;
    private BigDecimal montoEsperado;
    private BigDecimal montoReal;
    private BigDecimal diferencia;
}
