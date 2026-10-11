package com.example.pos.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CompraDomainTest {

    @Test
    @DisplayName("Debe crear una compra e incrementar total al agregar detalles")
    void crearCompra_RecalculaTotalCorrectamente() {
        CompraDomain compra = CompraDomain.crear(10L, 1L, "Pedido mensual");
        assertEquals(BigDecimal.ZERO, compra.getTotal());

        CompraDetalleDomain det1 = new CompraDetalleDomain(1L, "Arroz", 10, new BigDecimal("3.50"));
        CompraDetalleDomain det2 = new CompraDetalleDomain(2L, "Azúcar", 5, new BigDecimal("4.00"));

        compra.agregarDetalle(det1);
        compra.agregarDetalle(det2);

        // 10*3.50 + 5*4.00 = 35.00 + 20.00 = 55.00
        assertEquals(new BigDecimal("55.00"), compra.getTotal());
    }
}
