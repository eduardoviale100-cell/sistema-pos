package com.example.pos.service;

import com.example.pos.dto.AjusteStockRequestDto;
import com.example.pos.model.Inventario;
import com.example.pos.model.MovimientoInventario;
import com.example.pos.model.Producto;
import com.example.pos.repository.InventarioRepository;
import com.example.pos.repository.MovimientoInventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto producto;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(10);
        producto.setNombre("Mouse Inalámbrico");

        inventario = new Inventario();
        inventario.setId(1);
        inventario.setProducto(producto);
        inventario.setStockActual(15);
        inventario.setStockMinimo(5);
    }

    @Test
    @DisplayName("Debe ajustar stock de tipo ENTRADA sumando unidades")
    void ajustarStock_Entrada_SumaStock() {
        AjusteStockRequestDto dto = new AjusteStockRequestDto();
        dto.setTipo("ENTRADA");
        dto.setCantidad(10);
        dto.setMotivo("Devolución de cliente");

        when(inventarioRepository.findByProductoId(10)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArgument(0));

        Inventario resultado = inventarioService.ajustarStock(10, dto);

        assertEquals(25, resultado.getStockActual()); // 15 + 10
        verify(inventarioRepository).save(inventario);
        verify(movimientoInventarioRepository).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("Debe ajustar stock de tipo SALIDA restando unidades cuando hay stock suficiente")
    void ajustarStock_Salida_RestaStock() {
        AjusteStockRequestDto dto = new AjusteStockRequestDto();
        dto.setTipo("SALIDA");
        dto.setCantidad(5);
        dto.setMotivo("Producto dañado");

        when(inventarioRepository.findByProductoId(10)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArgument(0));

        Inventario resultado = inventarioService.ajustarStock(10, dto);

        assertEquals(10, resultado.getStockActual()); // 15 - 5
        verify(inventarioRepository).save(inventario);
        verify(movimientoInventarioRepository).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException al intentar una SALIDA mayor al stock actual")
    void ajustarStock_SalidaMayorAlStock_LanzaExcepcion() {
        AjusteStockRequestDto dto = new AjusteStockRequestDto();
        dto.setTipo("SALIDA");
        dto.setCantidad(30); // Actual es 15
        dto.setMotivo("Merma");

        when(inventarioRepository.findByProductoId(10)).thenReturn(Optional.of(inventario));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> inventarioService.ajustarStock(10, dto));
        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe ajustar stock fijando la cantidad exacta en tipo AJUSTE")
    void ajustarStock_AjusteFijo_ReemplazaStock() {
        AjusteStockRequestDto dto = new AjusteStockRequestDto();
        dto.setTipo("AJUSTE");
        dto.setCantidad(50);
        dto.setMotivo("Conteo físico anual");

        when(inventarioRepository.findByProductoId(10)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArgument(0));

        Inventario resultado = inventarioService.ajustarStock(10, dto);

        assertEquals(50, resultado.getStockActual());
        verify(inventarioRepository).save(inventario);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el tipo de operación no es válido")
    void ajustarStock_TipoInvalido_LanzaExcepcion() {
        AjusteStockRequestDto dto = new AjusteStockRequestDto();
        dto.setTipo("TRANSFERENCIA");
        dto.setCantidad(5);

        when(inventarioRepository.findByProductoId(10)).thenReturn(Optional.of(inventario));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> inventarioService.ajustarStock(10, dto));
        assertTrue(ex.getMessage().contains("Tipo de operación no válida"));
    }

    @Test
    @DisplayName("Debe listar productos con bajo stock")
    void listarBajoStock_DebeRetornarLista() {
        when(inventarioRepository.buscarProductosConBajoStock()).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.listarBajoStock();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(inventarioRepository).buscarProductosConBajoStock();
    }
}
