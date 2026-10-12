package com.example.pos.application.usecase.inventario;

import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.exception.StockInsuficienteInventarioException;
import com.example.pos.domain.model.inventario.InventarioDomain;
import com.example.pos.domain.model.inventario.MovimientoInventarioDomain;
import com.example.pos.domain.model.producto.ProductoDomain;
import com.example.pos.domain.model.usuario.RolUsuario;
import com.example.pos.domain.model.usuario.UsuarioDomain;
import com.example.pos.domain.repository.InventarioRepositoryPort;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioUseCaseTest {

    @Mock
    private InventarioRepositoryPort inventarioRepositoryPort;

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private AjustarStockInventarioUseCase ajustarStockInventarioUseCase;

    private ConsultarInventarioUseCase consultarInventarioUseCase;
    private ListarInventarioUseCase listarInventarioUseCase;
    private ListarMovimientosKardexUseCase listarMovimientosKardexUseCase;

    private ProductoDomain producto;
    private InventarioDomain inventario;
    private UsuarioDomain usuario;

    @BeforeEach
    void setUp() {
        consultarInventarioUseCase = new ConsultarInventarioUseCase(inventarioRepositoryPort, productoRepositoryPort);
        listarInventarioUseCase = new ListarInventarioUseCase(inventarioRepositoryPort, productoRepositoryPort);
        listarMovimientosKardexUseCase = new ListarMovimientosKardexUseCase(inventarioRepositoryPort);

        producto = new ProductoDomain(1L, "PROD-01", "Aceite Vegetal", null, new BigDecimal("8.50"), 20, true);
        inventario = new InventarioDomain(10L, 1L, "Aceite Vegetal", "PROD-01", 20, 5, LocalDateTime.now());
        usuario = new UsuarioDomain(2L, "Admin", "admin", "hash", RolUsuario.ADMIN, true, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("ConsultarInventario debe retornar inventario existente")
    void consultarInventario_Existente() {
        when(inventarioRepositoryPort.buscarPorProductoId(1L)).thenReturn(Optional.of(inventario));

        InventarioDomain res = consultarInventarioUseCase.ejecutar(1L);

        assertNotNull(res);
        assertEquals(20, res.getStockActual());
    }

    @Test
    @DisplayName("ConsultarInventario debe inicializar inventario en memoria si el producto existe pero aÃºn no tiene fila")
    void consultarInventario_SinFilaPrevia_UsaDatosProducto() {
        when(inventarioRepositoryPort.buscarPorProductoId(1L)).thenReturn(Optional.empty());
        when(productoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(producto));

        InventarioDomain res = consultarInventarioUseCase.ejecutar(1L);

        assertNotNull(res);
        assertEquals(1L, res.getProductoId());
        assertEquals(20, res.getStockActual());
    }

    @Test
    @DisplayName("AjustarStock con tipo ENTRADA debe incrementar stock y registrar movimiento")
    void ajustarStock_Entrada_Exitoso() {
        when(productoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepositoryPort.buscarPorProductoId(1L)).thenReturn(Optional.of(inventario));
        when(usuarioRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(usuario));
        when(inventarioRepositoryPort.guardar(any(InventarioDomain.class))).thenAnswer(i -> i.getArgument(0));

        AjustarStockCommand command = new AjustarStockCommand(1L, 10, "ENTRADA", 2L, "Lote nuevo de proveedor");

        InventarioDomain resultado = ajustarStockInventarioUseCase.ejecutar(command);

        assertEquals(30, resultado.getStockActual());
        assertEquals(30, producto.getStock());
        verify(inventarioRepositoryPort).registrarMovimiento(any(MovimientoInventarioDomain.class));
        verify(productoRepositoryPort).guardar(producto);
    }

    @Test
    @DisplayName("AjustarStock con tipo SALIDA debe decrementar stock y registrar movimiento")
    void ajustarStock_Salida_Exitoso() {
        when(productoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepositoryPort.buscarPorProductoId(1L)).thenReturn(Optional.of(inventario));
        when(usuarioRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(usuario));
        when(inventarioRepositoryPort.guardar(any(InventarioDomain.class))).thenAnswer(i -> i.getArgument(0));

        AjustarStockCommand command = new AjustarStockCommand(1L, 5, "SALIDA", 2L, "Merma por rotura");

        InventarioDomain resultado = ajustarStockInventarioUseCase.ejecutar(command);

        assertEquals(15, resultado.getStockActual());
        assertEquals(15, producto.getStock());
        verify(inventarioRepositoryPort).registrarMovimiento(any(MovimientoInventarioDomain.class));
    }

    @Test
    @DisplayName("AjustarStock con tipo SALIDA debe lanzar StockInsuficienteInventarioException si no hay stock")
    void ajustarStock_Salida_Insuficiente_LanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepositoryPort.buscarPorProductoId(1L)).thenReturn(Optional.of(inventario));

        AjustarStockCommand command = new AjustarStockCommand(1L, 50, "SALIDA", 2L, "Retiro mayor al stock");

        assertThrows(StockInsuficienteInventarioException.class, () -> ajustarStockInventarioUseCase.ejecutar(command));
    }

    @Test
    @DisplayName("AjustarStock con tipo AJUSTE debe asignar la cantidad absoluta de stock")
    void ajustarStock_AjusteAbsoluto_Exitoso() {
        when(productoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepositoryPort.buscarPorProductoId(1L)).thenReturn(Optional.of(inventario));
        when(usuarioRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(usuario));
        when(inventarioRepositoryPort.guardar(any(InventarioDomain.class))).thenAnswer(i -> i.getArgument(0));

        AjustarStockCommand command = new AjustarStockCommand(1L, 12, "AJUSTE", 2L, "Conteo fÃ­sico en almacÃ©n");

        InventarioDomain resultado = ajustarStockInventarioUseCase.ejecutar(command);

        assertEquals(12, resultado.getStockActual());
        assertEquals(12, producto.getStock());
    }

    @Test
    @DisplayName("ListarInventario y ListarMovimientos deben invocar sus puertos respectivos")
    void listar_CasosExitosos() {
        when(inventarioRepositoryPort.listarTodos()).thenReturn(List.of(inventario));
        when(inventarioRepositoryPort.listarBajoStock()).thenReturn(List.of(inventario));
        when(inventarioRepositoryPort.listarMovimientosPorProductoId(1L)).thenReturn(List.of());

        assertEquals(1, listarInventarioUseCase.listarTodos().size());
        assertEquals(1, listarInventarioUseCase.listarBajoStock().size());
        assertTrue(listarMovimientosKardexUseCase.listarPorProductoId(1L).isEmpty());
    }
}

