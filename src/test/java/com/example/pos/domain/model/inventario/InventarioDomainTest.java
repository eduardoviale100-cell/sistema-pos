package com.example.pos.domain.model.inventario;

import com.example.pos.domain.exception.StockInsuficienteInventarioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventarioDomainTest {

    @Test
    @DisplayName("Debe aumentar stock correctamente")
    void aumentarStock_Exitoso() {
        InventarioDomain inv = new InventarioDomain(1L, "Arroz Extra", "ARR-001", 10, 5);
        inv.aumentarStock(5);
        assertEquals(15, inv.getStockActual());
    }

    @Test
    @DisplayName("Debe disminuir stock correctamente si hay suficiente disponibilidad")
    void disminuirStock_Exitoso() {
        InventarioDomain inv = new InventarioDomain(1L, "Arroz Extra", "ARR-001", 10, 5);
        inv.disminuirStock(4);
        assertEquals(6, inv.getStockActual());
    }

    @Test
    @DisplayName("Debe lanzar StockInsuficienteInventarioException si se solicita mÃ¡s del stock disponible")
    void disminuirStock_Insuficiente_LanzaExcepcion() {
        InventarioDomain inv = new InventarioDomain(1L, "Arroz Extra", "ARR-001", 3, 5);
        assertThrows(StockInsuficienteInventarioException.class, () -> inv.disminuirStock(5));
    }

    @Test
    @DisplayName("Debe establecer stock absoluto correctamente")
    void establecerStock_Exitoso() {
        InventarioDomain inv = new InventarioDomain(1L, "Arroz Extra", "ARR-001", 10, 5);
        inv.establecerStock(25);
        assertEquals(25, inv.getStockActual());
    }

    @Test
    @DisplayName("Debe identificar si estÃ¡ en bajo stock segÃºn el stock mÃ­nimo configurado")
    void isBajoStock_DetectaCorrectamente() {
        InventarioDomain inv = new InventarioDomain(1L, "Arroz Extra", "ARR-001", 5, 5);
        assertTrue(inv.isBajoStock());

        inv.aumentarStock(1);
        assertFalse(inv.isBajoStock());
    }
}

