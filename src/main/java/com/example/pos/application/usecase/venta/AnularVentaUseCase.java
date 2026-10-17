package com.example.pos.application.usecase.venta;


import com.example.pos.domain.exception.VentaYaAnuladaException;
import com.example.pos.domain.exception.VentaNoEncontradaException;
import com.example.pos.domain.model.caja.MovimientoCajaDomain;
import com.example.pos.domain.model.inventario.MovimientoInventarioDomain;
import com.example.pos.domain.model.producto.ProductoDomain;
import com.example.pos.domain.model.caja.SesionCajaDomain;
import com.example.pos.domain.model.usuario.UsuarioDomain;
import com.example.pos.domain.model.venta.VentaDetalleDomain;
import com.example.pos.domain.model.venta.VentaDomain;
import com.example.pos.domain.repository.InventarioRepositoryPort;
import com.example.pos.domain.repository.MovimientoCajaRepositoryPort;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import com.example.pos.domain.repository.VentaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de Uso: Anular una venta emitida.
 * Reintegra el inventario, descuenta el efectivo de la sesión de caja si procede
 * y registra los movimientos de auditoría correspondientes.
 */
@Service
public class AnularVentaUseCase {

    private final VentaRepositoryPort ventaRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final SesionCajaRepositoryPort sesionCajaRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final MovimientoCajaRepositoryPort movimientoCajaRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;

    public AnularVentaUseCase(
            VentaRepositoryPort ventaRepositoryPort,
            ProductoRepositoryPort productoRepositoryPort,
            SesionCajaRepositoryPort sesionCajaRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort,
            MovimientoCajaRepositoryPort movimientoCajaRepositoryPort,
            InventarioRepositoryPort inventarioRepositoryPort) {
        this.ventaRepositoryPort = ventaRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.sesionCajaRepositoryPort = sesionCajaRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.movimientoCajaRepositoryPort = movimientoCajaRepositoryPort;
        this.inventarioRepositoryPort = inventarioRepositoryPort;
    }

    @Transactional
    public VentaDomain ejecutar(Long id, String motivo) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la venta es obligatorio.");
        }

        VentaDomain venta = ventaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> VentaNoEncontradaException.porId(id));

        // Validación de dominio (lanza VentaYaAnuladaException si ya estaba anulada)
        venta.anular();
        if (motivo != null && !motivo.isBlank()) {
            String obsActual = venta.getObservaciones() != null ? venta.getObservaciones() + " | " : "";
            venta.setObservaciones(obsActual + "ANULADA: " + motivo.trim());
        }

        // 1. Reintegrar stock a productos y sincronizar kardex
        for (VentaDetalleDomain det : venta.getDetalles()) {
            productoRepositoryPort.buscarPorId(det.getProductoId()).ifPresent(prod -> {
                // Sincronizar stock del producto con inventario antes de reintegrar
                inventarioRepositoryPort.buscarPorProductoId(prod.getId()).ifPresent(inv -> {
                    if (inv.getStockActual() != null && !inv.getStockActual().equals(prod.getStock())) {
                        prod.setStock(inv.getStockActual());
                    }
                });

                prod.incrementarStock(det.getCantidad());
                productoRepositoryPort.guardar(prod);

                inventarioRepositoryPort.buscarPorProductoId(prod.getId()).ifPresent(inv -> {
                    inv.setStockActual(prod.getStock());
                    inventarioRepositoryPort.guardar(inv);
                });

                MovimientoInventarioDomain movInv = new MovimientoInventarioDomain(
                        null,
                        prod.getId(),
                        prod.getNombre(),
                        "ENTRADA",
                        det.getCantidad(),
                        LocalDateTime.now(),
                        venta.getUsuarioId(),
                        venta.getUsuarioNombre(),
                        "ANULACION: " + venta.getCodigoFactura(),
                        "Reingreso por anulación de venta " + venta.getCodigoFactura()
                );
                inventarioRepositoryPort.registrarMovimiento(movInv);
            });
        }

        // 2. Revertir impacto en caja si hubo efectivo y la sesión sigue disponible
        BigDecimal totalEfectivo = venta.calcularTotalEfectivo();
        if (totalEfectivo.compareTo(BigDecimal.ZERO) > 0 && venta.getSesionCajaId() != null) {
            sesionCajaRepositoryPort.buscarPorId(venta.getSesionCajaId()).ifPresent(sesion -> {
                if (sesion.isAbierta()) {
                    sesion.registrarRetiro(totalEfectivo);
                    sesionCajaRepositoryPort.guardar(sesion);

                    UsuarioDomain usuario = null;
                    if (venta.getUsuarioId() != null) {
                        usuario = usuarioRepositoryPort.buscarPorId(venta.getUsuarioId()).orElse(null);
                    }

                    MovimientoCajaDomain movCaja = new MovimientoCajaDomain(
                            null,
                            sesion,
                            usuario,
                            "RETIRO",
                            totalEfectivo,
                            LocalDateTime.now(),
                            "ANULACION-" + venta.getCodigoFactura(),
                            "Egreso por anulación de venta " + venta.getCodigoFactura()
                    );
                    movimientoCajaRepositoryPort.guardar(movCaja);
                }
            });
        }

        return ventaRepositoryPort.guardar(venta);
    }
}
