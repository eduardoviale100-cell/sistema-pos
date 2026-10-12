package com.example.pos.infrastructure.input.rest.compra;

import com.example.pos.application.usecase.compra.ListarComprasUseCase;
import com.example.pos.application.usecase.compra.ObtenerCompraPorIdUseCase;
import com.example.pos.application.usecase.compra.RegistrarCompraUseCase;
import com.example.pos.domain.model.compra.CompraDomain;
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
class CompraRestControllerTest {

    @Mock
    private RegistrarCompraUseCase registrarCompraUseCase;

    @Mock
    private ListarComprasUseCase listarComprasUseCase;

    @Mock
    private ObtenerCompraPorIdUseCase obtenerCompraPorIdUseCase;

    @InjectMocks
    private CompraRestController compraRestController;

    private CompraDomain compra;

    @BeforeEach
    void setUp() {
        compra = CompraDomain.crear(10L, 1L, "Nota de compra");
        compra.setId(100L);
        compra.setTotal(new BigDecimal("120.00"));
    }

    @Test
    @DisplayName("POST /api/v2/compras debe registrar compra y retornar 201 Created")
    void registrarCompra_DebeRetornar201Created() {
        RegistrarCompraInputDto dto = new RegistrarCompraInputDto();
        dto.setProveedorId(10L);
        dto.setUsuarioId(1L);

        RegistrarCompraInputDto.DetalleCompraInputDto det = new RegistrarCompraInputDto.DetalleCompraInputDto();
        det.setProductoId(5L);
        det.setCantidad(10);
        det.setPrecioUnitario(new BigDecimal("12.00"));
        dto.setDetalles(List.of(det));

        when(registrarCompraUseCase.ejecutar(any())).thenReturn(compra);

        ResponseEntity<CompraDomain> response = compraRestController.registrarCompra(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().getId());
    }

    @Test
    @DisplayName("GET /api/v2/compras debe listar compras y retornar 200 OK")
    void listarCompras_DebeRetornar200OK() {
        when(listarComprasUseCase.ejecutar()).thenReturn(List.of(compra));

        ResponseEntity<List<CompraDomain>> response = compraRestController.listarCompras();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/v2/compras/{id} debe retornar compra por ID y 200 OK")
    void buscarPorId_DebeRetornar200OK() {
        when(obtenerCompraPorIdUseCase.ejecutar(100L)).thenReturn(compra);

        ResponseEntity<CompraDomain> response = compraRestController.buscarPorId(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100L, response.getBody().getId());
    }
}

