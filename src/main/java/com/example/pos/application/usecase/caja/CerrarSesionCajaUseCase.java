package com.example.pos.application.usecase.caja;

import com.example.pos.domain.model.caja.*;
import com.example.pos.domain.model.caja.*;

import com.example.pos.domain.exception.SesionCajaNoAbiertaException;
import com.example.pos.domain.model.caja.CajaDomain;
import com.example.pos.domain.model.caja.SesionCajaDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de Uso: Cerrar una sesión de caja activa y calcular el arqueo/diferencia.
 */
@Service
public class CerrarSesionCajaUseCase {

    private final SesionCajaRepositoryPort sesionCajaRepositoryPort;
    private final CajaRepositoryPort cajaRepositoryPort;

    public CerrarSesionCajaUseCase(
            SesionCajaRepositoryPort sesionCajaRepositoryPort,
            CajaRepositoryPort cajaRepositoryPort) {
        this.sesionCajaRepositoryPort = sesionCajaRepositoryPort;
        this.cajaRepositoryPort = cajaRepositoryPort;
    }

    @Transactional
    public SesionCajaDomain ejecutar(Long sesionId, BigDecimal montoRealContado) {
        if (montoRealContado == null || montoRealContado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto real contado no puede ser negativo ni nulo.");
        }

        SesionCajaDomain sesion = sesionCajaRepositoryPort.buscarPorId(sesionId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la sesión de caja con ID: " + sesionId));

        if (!sesion.isAbierta()) {
            throw SesionCajaNoAbiertaException.porSesion(sesionId);
        }

        // Ejecutar cierre en el dominio (calcula diferencia, fechaCierre y estado)
        sesion.cerrar(montoRealContado, LocalDateTime.now());

        // Actualizar estado de la caja física asociada
        CajaDomain caja = sesion.getCaja();
        if (caja != null) {
            caja.cerrar();
            cajaRepositoryPort.guardar(caja);
        }

        return sesionCajaRepositoryPort.guardar(sesion);
    }
}
