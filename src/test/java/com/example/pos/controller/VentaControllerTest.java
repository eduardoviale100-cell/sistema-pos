package com.example.pos.controller;

import com.example.pos.dto.VentaRequestDto;
import com.example.pos.model.Venta;
import com.example.pos.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    private Venta venta;
    private VentaRequestDto ventaDto;

    @BeforeEach
    void setUp() {
        venta = new Venta();
        venta.setId(1);
        venta.setTotal(new BigDecimal("150.00"));
        venta.setCodigoFactura("VTA-20260925-00001");

        ventaDto = new VentaRequestDto();
        ventaDto.setSesionCajaId(10);
        ventaDto.setUsuarioId(1);
        ventaDto.setDetalles(List.of());
        ventaDto.setPagos(List.of());
    }

    @Test
    @DisplayName("GET /api/ventas debe listar todas las ventas cuando no hay filtros")
    void listar_SinFiltros_DebeRetornar200YLista() {
        when(ventaService.listarVentas()).thenReturn(List.of(venta));

        ResponseEntity<List<Venta>> response = ventaController.listar(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(ventaService).listarVentas();
    }

    @Test
    @DisplayName("GET /api/ventas con sesionId y usuarioId debe filtrar por ambos")
    void listar_ConFiltroSesionYUsuario_DebeFiltrarPorAmbos() {
        when(ventaService.listarPorSesionYUsuario(10, 1)).thenReturn(List.of(venta));

        ResponseEntity<List<Venta>> response = ventaController.listar(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ventaService).listarPorSesionYUsuario(10, 1);
    }

    @Test
    @DisplayName("GET /api/ventas/{id} debe retornar 200 cuando la venta existe")
    void buscarPorId_CuandoExiste_DebeRetornar200() {
        when(ventaService.buscarPorId(1)).thenReturn(Optional.of(venta));

        ResponseEntity<Venta> response = ventaController.buscarPorId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    @DisplayName("GET /api/ventas/{id} debe retornar 404 cuando la venta no existe")
    void buscarPorId_CuandoNoExiste_DebeRetornar404() {
        when(ventaService.buscarPorId(99)).thenReturn(Optional.empty());

        ResponseEntity<Venta> response = ventaController.buscarPorId(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /api/ventas debe retornar 201 CREATED al registrar venta exitosamente")
    void registrarVenta_Exitosa_DebeRetornar201() {
        when(ventaService.registrarVenta(any(VentaRequestDto.class))).thenReturn(venta);

        ResponseEntity<?> response = ventaController.registrarVenta(ventaDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(venta, response.getBody());
    }

    @Test
    @DisplayName("POST /api/ventas debe retornar 400 BAD_REQUEST ante IllegalArgumentException")
    void registrarVenta_IllegalArgument_DebeRetornar400() {
        when(ventaService.registrarVenta(any(VentaRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Monto de pago insuficiente"));

        ResponseEntity<?> response = ventaController.registrarVenta(ventaDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Datos de Venta Inválidos", body.get("error"));
        assertEquals("Monto de pago insuficiente", body.get("mensaje"));
    }

    @Test
    @DisplayName("POST /api/ventas debe retornar 409 CONFLICT ante IllegalStateException")
    void registrarVenta_IllegalState_DebeRetornar409() {
        when(ventaService.registrarVenta(any(VentaRequestDto.class)))
                .thenThrow(new IllegalStateException("Stock insuficiente"));

        ResponseEntity<?> response = ventaController.registrarVenta(ventaDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Conflicto de Estado", body.get("error"));
        assertEquals("Stock insuficiente", body.get("mensaje"));
    }
}
