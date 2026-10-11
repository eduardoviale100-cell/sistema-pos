package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.AnularVentaUseCase;
import com.example.pos.application.usecase.ListarVentasUseCase;
import com.example.pos.application.usecase.ObtenerVentaPorIdUseCase;
import com.example.pos.application.usecase.RegistrarVentaUseCase;
import com.example.pos.domain.model.VentaDomain;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaRestControllerTest {

    @Mock
    private RegistrarVentaUseCase registrarVentaUseCase;

    @Mock
    private ObtenerVentaPorIdUseCase obtenerVentaPorIdUseCase;

    @Mock
    private ListarVentasUseCase listarVentasUseCase;

    @Mock
    private AnularVentaUseCase anularVentaUseCase;

    @InjectMocks
    private VentaRestController ventaRestController;

    private VentaDomain venta;

    @BeforeEach
    void setUp() {
        venta = VentaDomain.crear(10L, 1L, null, "Nota");
        venta.setId(100L);
        venta.setTotal(new BigDecimal("50.00"));
        venta.setCodigoFactura("VTA-20261009-00001");
    }

    @Test
    @DisplayName("POST /api/v2/ventas debe registrar venta y retornar 201 Created")
    void registrarVenta_DebeRetornar201Created() {
        RegistrarVentaInputDto dto = new RegistrarVentaInputDto();
        dto.setSesionCajaId(10L);
        dto.setUsuarioId(1L);

        RegistrarVentaInputDto.DetalleItemInputDto det = new RegistrarVentaInputDto.DetalleItemInputDto();
        det.setProductoId(5L);
        det.setCantidad(2);
        dto.setDetalles(List.of(det));

        RegistrarVentaInputDto.PagoItemInputDto pago = new RegistrarVentaInputDto.PagoItemInputDto();
        pago.setMetodoPagoId(1L);
        pago.setMonto(new BigDecimal("50.00"));
        dto.setPagos(List.of(pago));

        when(registrarVentaUseCase.ejecutar(any())).thenReturn(venta);

        ResponseEntity<VentaDomain> response = ventaRestController.registrarVenta(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().getId());
    }

    @Test
    @DisplayName("GET /api/v2/ventas debe listar ventas y retornar 200 OK")
    void listarVentas_DebeRetornar200OK() {
        when(listarVentasUseCase.ejecutar(null, null)).thenReturn(List.of(venta));

        ResponseEntity<List<VentaDomain>> response = ventaRestController.listarVentas(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/v2/ventas/{id} debe retornar venta por ID y 200 OK")
    void buscarPorId_DebeRetornar200OK() {
        when(obtenerVentaPorIdUseCase.ejecutar(100L)).thenReturn(venta);

        ResponseEntity<VentaDomain> response = ventaRestController.buscarPorId(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100L, response.getBody().getId());
    }

    @Test
    @DisplayName("PUT /api/v2/ventas/{id}/anular debe anular venta y retornar 200 OK")
    void anularVenta_DebeRetornar200OK() {
        venta.anular();
        when(anularVentaUseCase.ejecutar(100L, "Motivo")).thenReturn(venta);

        AnularVentaInputDto dto = new AnularVentaInputDto();
        dto.setMotivo("Motivo");

        ResponseEntity<VentaDomain> response = ventaRestController.anularVenta(100L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isAnulada());
    }
}
