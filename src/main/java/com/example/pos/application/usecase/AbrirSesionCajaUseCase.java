package com.example.pos.application.usecase;

import com.example.pos.domain.exception.CajaNoEncontradaException;
import com.example.pos.domain.exception.CajaYaAbiertaException;
import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import com.example.pos.domain.model.CajaDomain;
import com.example.pos.domain.model.SesionCajaDomain;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de Uso: Abrir una nueva sesión de caja (Turno de trabajo).
 */
@Service
public class AbrirSesionCajaUseCase {

    private final CajaRepositoryPort cajaRepositoryPort;
    private final SesionCajaRepositoryPort sesionCajaRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public AbrirSesionCajaUseCase(
            CajaRepositoryPort cajaRepositoryPort,
            SesionCajaRepositoryPort sesionCajaRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort) {
        this.cajaRepositoryPort = cajaRepositoryPort;
        this.sesionCajaRepositoryPort = sesionCajaRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Transactional
    public SesionCajaDomain ejecutar(Long cajaId, Long usuarioId, BigDecimal montoInicial) {
        if (montoInicial == null || montoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto inicial no puede ser nulo ni negativo.");
        }

        CajaDomain caja = cajaRepositoryPort.buscarPorId(cajaId)
                .orElseThrow(() -> CajaNoEncontradaException.porId(cajaId));

        if (sesionCajaRepositoryPort.existeSesionAbierta(cajaId) || caja.isAbierta()) {
            throw new CajaYaAbiertaException(caja.getNombre());
        }

        UsuarioDomain usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> UsuarioNoEncontradoException.porId(usuarioId));

        if (!Boolean.TRUE.equals(usuario.isActivo())) {
            throw new IllegalStateException("El usuario '" + usuario.getNombre() + "' no se encuentra activo.");
        }

        // Abrir la caja física
        caja.abrir();
        cajaRepositoryPort.guardar(caja);

        // Crear la sesión de caja
        SesionCajaDomain nuevaSesion = SesionCajaDomain.iniciar(
                caja,
                usuario,
                montoInicial,
                LocalDateTime.now()
        );

        return sesionCajaRepositoryPort.guardar(nuevaSesion);
    }
}
