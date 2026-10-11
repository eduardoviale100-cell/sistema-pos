package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta abrir una caja que ya cuenta con una sesión abierta.
 */
public class CajaYaAbiertaException extends RuntimeException {

    public CajaYaAbiertaException(String nombreCaja) {
        super(String.format("La caja '%s' ya cuenta con una sesión abierta activa.", nombreCaja));
    }
}
