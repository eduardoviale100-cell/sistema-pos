package com.example.pos.application.usecase.caja;

import com.example.pos.domain.exception.SesionCajaNoAbiertaException;
import com.example.pos.domain.model.caja.CajaDomain;
import com.example.pos.domain.model.caja.MovimientoCajaDomain;
import com.example.pos.domain.model.caja.SesionCajaDomain;
import com.example.pos.domain.model.venta.VentaDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import com.example.pos.domain.repository.MovimientoCajaRepositoryPort;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import com.example.pos.domain.repository.VentaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de Uso: Cerrar una sesion de caja activa con arqueo y consolidacion financiera.
 *
 * Flujo transaccional:
 *  1. Valida que la sesion exista y este ABIERTA.
 *  2. Recalcula el monto esperado = monto_inicial + efectivo_ventas + ingresos_manuales - retiros.
 *  3. Registra montoReal, diferencia, fechaCierre y estado=CERRADA en la sesion.
 *  4. Cierra la caja fisica y auto-cierra cualquier sesion huerfana remanente.
 *
 * Manejo de nulls: todos los montos usan BigDecimal.ZERO como valor por defecto
 * para evitar NullPointerException en operaciones aritmeticas.
 */
@Service
public class CerrarSesionCajaUseCase {

    private final SesionCajaRepositoryPort sesionCajaRepositoryPort;
    private final CajaRepositoryPort cajaRepositoryPort;
    private final VentaRepositoryPort ventaRepositoryPort;
    private final MovimientoCajaRepositoryPort movimientoCajaRepositoryPort;

    public CerrarSesionCajaUseCase(
            SesionCajaRepositoryPort sesionCajaRepositoryPort,
            CajaRepositoryPort cajaRepositoryPort,
            VentaRepositoryPort ventaRepositoryPort,
            MovimientoCajaRepositoryPort movimientoCajaRepositoryPort) {
        this.sesionCajaRepositoryPort = sesionCajaRepositoryPort;
        this.cajaRepositoryPort = cajaRepositoryPort;
        this.ventaRepositoryPort = ventaRepositoryPort;
        this.movimientoCajaRepositoryPort = movimientoCajaRepositoryPort;
    }

    @Transactional
    public SesionCajaDomain ejecutar(Long sesionId, BigDecimal montoRealContado) {

        // --- Guardias de entrada ---
        if (sesionId == null) {
            throw new IllegalArgumentException("El ID de sesion es obligatorio.");
        }
        if (montoRealContado == null || montoRealContado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto real contado no puede ser nulo ni negativo.");
        }

        // --- Cargar sesion activa ---
        SesionCajaDomain sesion = sesionCajaRepositoryPort.buscarPorId(sesionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontro la sesion de caja con ID: " + sesionId));

        if (!sesion.isAbierta()) {
            throw SesionCajaNoAbiertaException.porSesion(sesionId);
        }

        // --- 1. Monto inicial (con guardia de null) ---
        BigDecimal montoInicial = coalesce(sesion.getMontoInicial(), BigDecimal.ZERO);

        // --- 2. Ventas en efectivo durante el turno ---
        // Solo ventas asociadas a esta sesion y no anuladas
        List<VentaDomain> ventas = ventaRepositoryPort.listarPorSesionCajaId(sesionId);
        BigDecimal totalVentasEfectivo = ventas.stream()
                .filter(v -> v != null && !"ANULADA".equalsIgnoreCase(v.getEstado()))
                .map(v -> coalesce(v.calcularTotalEfectivo(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- 3. Movimientos manuales de caja ---
        // Ingresos manuales: tipo INGRESO cuya referencia no sea una venta (VTA-)
        // Egresos: tipo RETIRO o EGRESO
        List<MovimientoCajaDomain> movimientos =
                movimientoCajaRepositoryPort.listarPorSesionCajaId(sesionId);

        BigDecimal otrosIngresos = movimientos.stream()
                .filter(m -> m != null
                        && "INGRESO".equalsIgnoreCase(m.getTipoMovimiento())
                        && (m.getReferencia() == null
                            || !m.getReferencia().toUpperCase().startsWith("VTA-")))
                .map(m -> coalesce(m.getMonto(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal otrosEgresos = movimientos.stream()
                .filter(m -> m != null
                        && ("RETIRO".equalsIgnoreCase(m.getTipoMovimiento())
                            || "EGRESO".equalsIgnoreCase(m.getTipoMovimiento())))
                .map(m -> coalesce(m.getMonto(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- 4. Calcular monto esperado en gaveta ---
        // Formula: monto_inicial + efectivo_ventas + ingresos_manuales - retiros
        BigDecimal montoEsperadoCalculado = montoInicial
                .add(totalVentasEfectivo)
                .add(otrosIngresos)
                .subtract(otrosEgresos);

        // Guardar el monto esperado calculado en la sesion antes de cerrar
        sesion.setMontoEsperado(montoEsperadoCalculado);

        // --- 5. Cerrar la sesion en el agregado de dominio ---
        // Asigna: montoReal, diferencia (montoReal - montoEsperado),
        //         fechaCierre = ahora, estado = CERRADA
        sesion.cerrar(montoRealContado, LocalDateTime.now());

        // --- 6. Cerrar la caja fisica y limpiar sesiones huerfanas ---
        CajaDomain caja = sesion.getCaja();
        if (caja != null) {
            caja.cerrar();
            cajaRepositoryPort.guardar(caja);

            // Auto-cerrar cualquier otra sesion ABIERTA remanente de esta caja
            List<SesionCajaDomain> sesionesHuerfanas =
                    sesionCajaRepositoryPort.buscarSesionesAbiertasPorCajaId(caja.getId());

            for (SesionCajaDomain huerfana : sesionesHuerfanas) {
                if (huerfana.getId() != null && !huerfana.getId().equals(sesionId)) {
                    BigDecimal saldo = coalesce(huerfana.getMontoEsperado(),
                                         coalesce(huerfana.getMontoInicial(), BigDecimal.ZERO));
                    huerfana.cerrar(saldo, LocalDateTime.now());
                    sesionCajaRepositoryPort.guardar(huerfana);
                }
            }
        }

        // --- 7. Persistir la sesion cerrada ---
        return sesionCajaRepositoryPort.guardar(sesion);
    }

    /**
     * Equivalente a COALESCE de SQL: retorna el primer valor no nulo.
     */
    private static BigDecimal coalesce(BigDecimal valor, BigDecimal defecto) {
        return valor != null ? valor : defecto;
    }
}