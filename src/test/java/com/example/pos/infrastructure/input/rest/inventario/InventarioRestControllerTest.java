package com.example.pos.infrastructure.input.rest.inventario;

import com.example.pos.application.usecase.inventario.AjustarStockInventarioUseCase;
import com.example.pos.application.usecase.inventario.ConsultarInventarioUseCase;
import com.example.pos.application.usecase.inventario.ListarInventarioUseCase;
import com.example.pos.application.usecase.inventario.ListarMovimientosKardexUseCase;
import com.example.pos.domain.model.inventario.InventarioDomain;
import com.example.pos.domain.model.inventario.MovimientoInventarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioRestControllerTest {

    @Mock
    private ConsultarInventarioUseCase consultarInventarioUseCase;

    @Mock
    private AjustarStockInventarioUseCase ajustarStockInventarioUseCase;

    @Mock
    private ListarInventarioUseCase listarInventarioUseCase;

    @Mock
    private ListarMovimientosKardexUseCase listarMovimientosKardexUseCase;

    @InjectMocks
    private InventarioRestController inventarioRestController;

    private InventarioDomain inventario;

    @BeforeEach
    void setUp() {
        inventario = new InventarioDomain(10L, 1L, "Producto Demo", "DEMO-01", 15, 5, LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/v2/inventario debe listar todo y retornar 200 OK")
    void listarTodo_DebeRetornar200OK() {
        when(listarInventarioUseCase.listarTodos()).thenReturn(List.of(inventario));

        ResponseEntity<List<InventarioDomain>> response = inventarioRestController.listarTodo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("GET /api/v2/inventario/producto/{id} debe retornar inventario por producto y 200 OK")
    void buscarPorProductoId_DebeRetornar200OK() {
        when(consultarInventarioUseCase.ejecutar(1L)).thenReturn(inventario);

        ResponseEntity<InventarioDomain> response = inventarioRestController.buscarPorProductoId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(15, response.getBody().getStockActual());
    }

    @Test
    @DisplayName("GET /api/v2/inventario/alertas-bajo-stock debe retornar lista y 200 OK")
    void listarBajoStock_DebeRetornar200OK() {
        when(listarInventarioUseCase.listarBajoStock()).thenReturn(List.of(inventario));

        ResponseEntity<List<InventarioDomain>> response = inventarioRestController.listarBajoStock();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("PATCH /api/v2/inventario/producto/{id}/ajuste debe procesar y retornar 200 OK")
    void ajustarStock_DebeRetornar200OK() {
        when(ajustarStockInventarioUseCase.ejecutar(any())).thenReturn(inventario);

        AjustarStockInputDto dto = new AjustarStockInputDto();
        dto.setCantidad(5);
        dto.setTipo("ENTRADA");
        dto.setMotivo("ReposiciÃ³n");

        ResponseEntity<InventarioDomain> response = inventarioRestController.ajustarStock(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(15, response.getBody().getStockActual());
    }

    @Test
    @DisplayName("GET /api/v2/inventario/producto/{id}/movimientos debe retornar lista de movimientos y 200 OK")
    void listarMovimientosPorProducto_DebeRetornar200OK() {
        MovimientoInventarioDomain mov = new MovimientoInventarioDomain(1L, "ENTRADA", 5, 2L, "Manual", "Test");
        when(listarMovimientosKardexUseCase.listarPorProductoId(1L)).thenReturn(List.of(mov));

        ResponseEntity<List<MovimientoInventarioDomain>> response = inventarioRestController.listarMovimientosPorProducto(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}

