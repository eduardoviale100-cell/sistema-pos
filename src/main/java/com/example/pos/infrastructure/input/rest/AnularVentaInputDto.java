package com.example.pos.infrastructure.input.rest;

import lombok.Data;

/**
 * DTO de entrada para la anulación de una venta.
 */
@Data
public class AnularVentaInputDto {

    private String motivo;
}
