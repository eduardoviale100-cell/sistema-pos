package com.example.pos.application.usecase;

import com.example.pos.domain.exception.SesionCajaNoAbiertaException;
import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import com.example.pos.domain.model.MovimientoCajaDomain;
import com.example.pos.domain.model.SesionCajaDomain;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.MovimientoCajaRepositoryPort;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de Uso: Registrar movimientos manuales de caja (Ingresos y Retiros).
 */
@Service
public class RegistrarMovimientoCajaUseCase {

    private final SesionCajaRepositoryPort sesionCajaRepositoryPort;
    private final MovimientoCajaRepositoryPort movimientoCajaRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public RegistrarMovimientoCajaUseCase(
            SesionCajaRepositoryPort sesionCajaRepositoryPort,
            MovimientoCajaRepositoryPort movimientoCajaRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort) {
        this.sesionCajaRepositoryPort = sesionCajaRepositoryPort;
        this.movimientoCajaRepositoryPort = movimientoCajaRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Transactional
    public MovimientoCajaDomain ejecutar(Long sesionId, Long usuarioId, String tipoMovimiento,
                                         BigDecimal monto, String referencia, String observaciones) {
        SesionCajaDomain sesion = sesionCajaRepositoryPort.buscarPorId(sesionId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la sesión de caja con ID: " + sesionId));

        if (!sesion.isAbierta()) {
            throw SesionCajaNoAbiertaException.porSesion(sesionId);
        }

        UsuarioDomain usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                    .orElseThrow(() -> UsuarioNoEncontradoException.porId(usuarioId));
        }

        String tipoNormalizado = (tipoMovimiento != null) ? tipoMovimiento.trim().toUpperCase() : "";
        if ("EGRESO".equals(tipoNormalizado)) {
            tipoNormalizado = "RETIRO";
        }

        // Actualizar el monto esperado en la sesión de dominio según el tipo de movimiento
        if ("INGRESO".equals(tipoNormalizado)) {
            sesion.registrarIngreso(monto);
        } else if ("RETIRO".equals(tipoNormalizado)) {
            sesion.registrarRetiro(monto);
        } else {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + tipoMovimiento);
        }

        sesionCajaRepositoryPort.guardar(sesion);

        MovimientoCajaDomain movimiento = new MovimientoCajaDomain(
                null,
                sesion,
                usuario,
                tipoNormalizado,
                monto,
                LocalDateTime.now(),
                referencia,
                observaciones
        );

        return movimientoCajaRepositoryPort.guardar(movimiento);
    }
}
