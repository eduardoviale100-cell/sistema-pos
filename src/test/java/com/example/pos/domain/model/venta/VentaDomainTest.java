package com.example.pos.domain.model.venta;

import com.example.pos.domain.exception.MontoPagoInsuficienteException;
import com.example.pos.domain.exception.VentaYaAnuladaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VentaDomainTest {

    @Test
    @DisplayName("Debe crear una venta con valores iniciales correctos y recalcular total al agregar detalles")
    void crearVenta_DebeRecalcularTotalCorrectamente() {
        VentaDomain venta = VentaDomain.crear(1L, 2L, 3L, "Venta en mostrador");
        assertEquals("COMPLETADA", venta.getEstado());
        assertEquals(BigDecimal.ZERO, venta.getTotal());

        VentaDetalleDomain det1 = new VentaDetalleDomain(10L, "Coca Cola", 2, new BigDecimal("2.50"));
        VentaDetalleDomain det2 = new VentaDetalleDomain(20L, "Galleta Oreo", 3, new BigDecimal("1.00"));

        venta.agregarDetalle(det1);
        venta.agregarDetalle(det2);

        // 2*2.50 + 3*1.00 = 5.00 + 3.00 = 8.00
        assertEquals(new BigDecimal("8.00"), venta.getTotal());
    }

    @Test
    @DisplayName("Debe validar pago suficiente cuando el total pagado cubre o excede el total de venta")
    void validarPagoSuficiente_ConMontoCorrecto_NoLanzaExcepcion() {
        VentaDomain venta = VentaDomain.crear(1L, 2L, null, null);
        venta.agregarDetalle(new VentaDetalleDomain(10L, "Producto A", 1, new BigDecimal("50.00")));

        PagoDomain pagoEfectivo = new PagoDomain(1L, "Efectivo", new BigDecimal("50.00"));
        venta.agregarPago(pagoEfectivo);

        assertDoesNotThrow(venta::validarPagoSuficiente);
        assertEquals(new BigDecimal("50.00"), venta.calcularTotalEfectivo());
        assertEquals(new BigDecimal("50.00"), venta.calcularTotalPagado());
    }

    @Test
    @DisplayName("Debe lanzar MontoPagoInsuficienteException si el total pagado es menor al total de la venta")
    void validarPagoSuficiente_ConMontoInsuficiente_LanzaExcepcion() {
        VentaDomain venta = VentaDomain.crear(1L, 2L, null, null);
        venta.agregarDetalle(new VentaDetalleDomain(10L, "Producto A", 1, new BigDecimal("100.00")));

        PagoDomain pagoParcial = new PagoDomain(1L, "Efectivo", new BigDecimal("80.00"));
        venta.agregarPago(pagoParcial);

        assertThrows(MontoPagoInsuficienteException.class, venta::validarPagoSuficiente);
    }

    @Test
    @DisplayName("Debe anular la venta exitosamente")
    void anular_VentaActiva_CambiaEstadoAAnulada() {
        VentaDomain venta = VentaDomain.crear(1L, 2L, null, null);
        assertFalse(venta.isAnulada());

        venta.anular();
        assertTrue(venta.isAnulada());
        assertEquals("ANULADA", venta.getEstado());
    }

    @Test
    @DisplayName("Debe lanzar VentaYaAnuladaException si se intenta anular una venta previamente anulada")
    void anular_VentaPreviamenteAnulada_LanzaExcepcion() {
        VentaDomain venta = VentaDomain.crear(1L, 2L, null, null);
        venta.anular();

        assertThrows(VentaYaAnuladaException.class, venta::anular);
    }
}

