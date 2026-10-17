package com.example.pos.application.usecase.caja;

import com.example.pos.domain.exception.CajaNoEncontradaException;
import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import com.example.pos.domain.model.caja.CajaDomain;
import com.example.pos.domain.model.caja.SesionCajaDomain;
import com.example.pos.domain.model.usuario.UsuarioDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de Uso: Abrir una nueva sesion de caja (Turno de trabajo).
 *
 * Logica de negocio:
 *  - Valida monto inicial no nulo ni negativo.
 *  - Valida que la caja exista y el usuario este activo.
 *  - Tolerancia a sesiones huerfanas/fantasma: si existen sesiones ABIERTA
 *    previas para esta caja o este usuario (por caidas o cierres abruptos),
 *    se auto-cierran de forma limpia sin bloquear al cajero.
 *  - Abre la caja fisicamente y crea la nueva sesion de turno.
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

        // --- Guardias de entrada ---
        if (cajaId == null) {
            throw new IllegalArgumentException("El ID de caja es obligatorio.");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID de usuario es obligatorio.");
        }
        if (montoInicial == null || montoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto inicial no puede ser nulo ni negativo.");
        }

        // --- Cargar entidades de dominio ---
        CajaDomain caja = cajaRepositoryPort.buscarPorId(cajaId)
                .orElseThrow(() -> CajaNoEncontradaException.porId(cajaId));

        UsuarioDomain usuario = usuarioRepositoryPort.buscarPorId(usuarioId)
                .orElseThrow(() -> UsuarioNoEncontradoException.porId(usuarioId));

        if (!Boolean.TRUE.equals(usuario.isActivo())) {
            throw new IllegalStateException(
                    "El usuario '" + usuario.getNombre() + "' no se encuentra activo en el sistema.");
        }

        // --- Tolerancia a sesiones huerfanas de la caja ---
        // Si por un apagon o cierre abrupto del navegador quedaron sesiones ABIERTA
        // para esta caja, se auto-cierran sin bloquear al cajero.
        List<SesionCajaDomain> sesionesAbiertasCaja =
                sesionCajaRepositoryPort.buscarSesionesAbiertasPorCajaId(cajaId);

        for (SesionCajaDomain sesionAnterior : sesionesAbiertasCaja) {
            BigDecimal saldoCierre = sesionAnterior.getMontoEsperado() != null
                    ? sesionAnterior.getMontoEsperado()
                    : (sesionAnterior.getMontoInicial() != null
                        ? sesionAnterior.getMontoInicial()
                        : BigDecimal.ZERO);
            sesionAnterior.cerrar(saldoCierre, LocalDateTime.now());
            sesionCajaRepositoryPort.guardar(sesionAnterior);
        }

        // --- Tolerancia a sesiones huerfanas del usuario en otras cajas ---
        // Evita que un cajero quede "doble-registrado" en dos cajas simultaneas.
        List<SesionCajaDomain> sesionesAbiertasUsuario =
                sesionCajaRepositoryPort.buscarSesionesAbiertasPorUsuarioId(usuarioId);

        for (SesionCajaDomain sesionUsuario : sesionesAbiertasUsuario) {
            if (sesionUsuario.getCaja() != null && !cajaId.equals(sesionUsuario.getCaja().getId())) {
                BigDecimal saldoCierre = sesionUsuario.getMontoEsperado() != null
                        ? sesionUsuario.getMontoEsperado()
                        : (sesionUsuario.getMontoInicial() != null
                            ? sesionUsuario.getMontoInicial()
                            : BigDecimal.ZERO);
                sesionUsuario.cerrar(saldoCierre, LocalDateTime.now());
                sesionCajaRepositoryPort.guardar(sesionUsuario);

                // Cerrar tambien la caja fisica de esa sesion huerfana
                CajaDomain otraCaja = sesionUsuario.getCaja();
                if (otraCaja != null) {
                    otraCaja.cerrar();
                    cajaRepositoryPort.guardar(otraCaja);
                }
            }
        }

        // --- Abrir la caja fisica y crear la nueva sesion de turno ---
        caja.abrir();
        cajaRepositoryPort.guardar(caja);

        SesionCajaDomain nuevaSesion = SesionCajaDomain.iniciar(
                caja,
                usuario,
                montoInicial,
                LocalDateTime.now()
        );

        return sesionCajaRepositoryPort.guardar(nuevaSesion);
    }
}