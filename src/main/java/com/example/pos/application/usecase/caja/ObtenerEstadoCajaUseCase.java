package com.example.pos.application.usecase.caja;

import com.example.pos.domain.exception.CajaNoEncontradaException;
import com.example.pos.domain.model.caja.CajaDomain;
import com.example.pos.domain.model.caja.MovimientoCajaDomain;
import com.example.pos.domain.model.caja.SesionCajaDomain;
import com.example.pos.domain.model.venta.PagoDomain;
import com.example.pos.domain.model.venta.VentaDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import com.example.pos.domain.repository.MovimientoCajaRepositoryPort;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import com.example.pos.domain.repository.VentaRepositoryPort;
import com.example.pos.infrastructure.input.rest.caja.EstadoActualCajaDto;
import com.example.pos.infrastructure.input.rest.caja.ResumenTurnoCajaDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Caso de Uso: Consultar el estado en tiempo real de una caja física y consolidar
 * el resumen del turno activo si se encuentra abierta. Auto-sincroniza inconsistencias de estado.
 */
@Service
public class ObtenerEstadoCajaUseCase {

    private final CajaRepositoryPort cajaRepositoryPort;
    private final SesionCajaRepositoryPort sesionCajaRepositoryPort;
    private final VentaRepositoryPort ventaRepositoryPort;
    private final MovimientoCajaRepositoryPort movimientoCajaRepositoryPort;

    public ObtenerEstadoCajaUseCase(
            CajaRepositoryPort cajaRepositoryPort,
            SesionCajaRepositoryPort sesionCajaRepositoryPort,
            VentaRepositoryPort ventaRepositoryPort,
            MovimientoCajaRepositoryPort movimientoCajaRepositoryPort) {
        this.cajaRepositoryPort = cajaRepositoryPort;
        this.sesionCajaRepositoryPort = sesionCajaRepositoryPort;
        this.ventaRepositoryPort = ventaRepositoryPort;
        this.movimientoCajaRepositoryPort = movimientoCajaRepositoryPort;
    }

    @Transactional
    public EstadoActualCajaDto ejecutar(Long cajaId) {
        if (cajaId == null) {
            throw new IllegalArgumentException("El ID de la caja es obligatorio.");
        }

        CajaDomain caja = cajaRepositoryPort.buscarPorId(cajaId)
                .orElseThrow(() -> CajaNoEncontradaException.porId(cajaId));

        Optional<SesionCajaDomain> sesionOpt = sesionCajaRepositoryPort.buscarSesionActivaPorCajaId(cajaId);

        // Auto-curación de inconsistencias (Auto-heal):
        // Si hay una sesión activa pero la caja dice CERRADA -> corregir caja a ABIERTA
        // Si NO hay sesión activa pero la caja dice ABIERTA -> corregir caja a CERRADA
        if (sesionOpt.isPresent() && !caja.isAbierta()) {
            caja.setEstado("ABIERTA");
            cajaRepositoryPort.guardar(caja);
        } else if (sesionOpt.isEmpty() && caja.isAbierta()) {
            caja.setEstado("CERRADA");
            cajaRepositoryPort.guardar(caja);
        }

        if (sesionOpt.isEmpty()) {
            return EstadoActualCajaDto.builder()
                    .cajaId(caja.getId())
                    .nombreCaja(caja.getNombre())
                    .estadoCaja(caja.getEstado())
                    .sesionActiva(null)
                    .resumenTurno(null)
                    .build();
        }

        SesionCajaDomain sesion = sesionOpt.get();
        Long sesionId = sesion.getId();

        // Calcular resumen financiero en tiempo real
        BigDecimal montoInicial = (sesion.getMontoInicial() != null) ? sesion.getMontoInicial() : BigDecimal.ZERO;

        List<VentaDomain> ventas = ventaRepositoryPort.listarPorSesionCajaId(sesionId);
        List<VentaDomain> ventasActivas = ventas.stream()
                .filter(v -> !"ANULADA".equalsIgnoreCase(v.getEstado()))
                .toList();

        BigDecimal totalVentasEfectivo = BigDecimal.ZERO;
        BigDecimal totalVentasOtrosMetodos = BigDecimal.ZERO;
        BigDecimal totalVentas = BigDecimal.ZERO;

        for (VentaDomain v : ventasActivas) {
            totalVentas = totalVentas.add(v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO);
            for (PagoDomain p : v.getPagos()) {
                if (p.esEfectivo()) {
                    totalVentasEfectivo = totalVentasEfectivo.add(p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO);
                } else {
                    totalVentasOtrosMetodos = totalVentasOtrosMetodos.add(p.getMonto() != null ? p.getMonto() : BigDecimal.ZERO);
                }
            }
        }

        List<MovimientoCajaDomain> movimientos = movimientoCajaRepositoryPort.listarPorSesionCajaId(sesionId);
        BigDecimal totalOtrosIngresos = movimientos.stream()
                .filter(m -> "INGRESO".equalsIgnoreCase(m.getTipoMovimiento()) &&
                        (m.getReferencia() == null || !m.getReferencia().toUpperCase().startsWith("VTA-")))
                .map(MovimientoCajaDomain::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOtrosEgresos = movimientos.stream()
                .filter(m -> ("RETIRO".equalsIgnoreCase(m.getTipoMovimiento()) || "EGRESO".equalsIgnoreCase(m.getTipoMovimiento())))
                .map(MovimientoCajaDomain::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montoEsperado = montoInicial.add(totalVentasEfectivo).add(totalOtrosIngresos).subtract(totalOtrosEgresos);

        ResumenTurnoCajaDto resumen = ResumenTurnoCajaDto.builder()
                .sesionId(sesion.getId())
                .cajaId(caja.getId())
                .nombreCaja(caja.getNombre())
                .usuarioId(sesion.getUsuario() != null ? sesion.getUsuario().getId() : null)
                .nombreUsuario(sesion.getUsuario() != null ? sesion.getUsuario().getNombre() : null)
                .fechaApertura(sesion.getFechaApertura())
                .fechaCierre(sesion.getFechaCierre())
                .estado(sesion.getEstado())
                .montoInicial(montoInicial)
                .totalVentasEfectivo(totalVentasEfectivo)
                .totalVentasOtrosMetodos(totalVentasOtrosMetodos)
                .totalVentas(totalVentas)
                .cantidadVentas(ventasActivas.size())
                .totalOtrosIngresos(totalOtrosIngresos)
                .totalOtrosEgresos(totalOtrosEgresos)
                .montoEsperado(montoEsperado)
                .montoReal(sesion.getMontoReal())
                .diferencia(sesion.getDiferencia())
                .build();

        return EstadoActualCajaDto.builder()
                .cajaId(caja.getId())
                .nombreCaja(caja.getNombre())
                .estadoCaja(caja.getEstado())
                .sesionActiva(sesion)
                .resumenTurno(resumen)
                .build();
    }
}
