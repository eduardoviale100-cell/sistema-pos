package com.example.pos.domain.exception;

/**
 * Excepción de dominio lanzada cuando una operación requiere una sesión de caja abierta y no la encuentra.
 */
public class SesionCajaNoAbiertaException extends RuntimeException {

    public SesionCajaNoAbiertaException(String mensaje) {
        super(mensaje);
    }

    public static SesionCajaNoAbiertaException porCaja(Long cajaId) {
        return new SesionCajaNoAbiertaException(
            String.format("No existe una sesión de caja ABIERTA para la caja con ID %d.", cajaId)
        );
    }

    public static SesionCajaNoAbiertaException porSesion(Long sesionId) {
        return new SesionCajaNoAbiertaException(
            String.format("La sesión de caja con ID %d no se encuentra ABIERTA.", sesionId)
        );
    }
}
