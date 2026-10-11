package com.example.pos.application.usecase;

import com.example.pos.domain.exception.*;
import com.example.pos.domain.model.*;
import com.example.pos.domain.repository.*;
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
class VentaUseCaseTest {

    @Mock
    private VentaRepositoryPort ventaRepositoryPort;

    @Mock
    private SesionCajaRepositoryPort sesionCajaRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    @Mock
    private MovimientoCajaRepositoryPort movimientoCajaRepositoryPort;

    @Mock
    private InventarioRepositoryPort inventarioRepositoryPort;

    @InjectMocks
    private RegistrarVentaUseCase registrarVentaUseCase;

    @InjectMocks
    private AnularVentaUseCase anularVentaUseCase;

    private ObtenerVentaPorIdUseCase obtenerVentaPorIdUseCase;
    private ListarVentasUseCase listarVentasUseCase;

    private SesionCajaDomain sesionAbierta;
    private UsuarioDomain usuarioActivo;
    private ProductoDomain productoStock;
    private MetodoPagoDomain metodoEfectivo;

    @BeforeEach
    void setUp() {
        obtenerVentaPorIdUseCase = new ObtenerVentaPorIdUseCase(ventaRepositoryPort);
        listarVentasUseCase = new ListarVentasUseCase(ventaRepositoryPort);

        CajaDomain caja = new CajaDomain(1L, "Caja Principal", "ABIERTA");
        usuarioActivo = new UsuarioDomain(1L, "Cajero 1", "cajero1", "hash", RolUsuario.CAJERO, true, LocalDateTime.now(), null);
        sesionAbierta = SesionCajaDomain.iniciar(caja, usuarioActivo, new BigDecimal("100.00"), LocalDateTime.now());
        sesionAbierta.setId(10L);

        productoStock = new ProductoDomain(5L, "PROD-001", "Gaseosa Cola", null, new BigDecimal("10.00"), 20, true);
        metodoEfectivo = new MetodoPagoDomain(1L, "Efectivo", true);
    }

    @Test
    @DisplayName("RegistrarVenta debe procesar exitosamente y retornar la venta guardada")
    void registrarVenta_Exitoso() {
        when(sesionCajaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioActivo));
        when(ventaRepositoryPort.contarTotalVentas()).thenReturn(42L);
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(productoStock));
        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(metodoEfectivo));
        when(inventarioRepositoryPort.buscarPorProductoId(5L)).thenReturn(Optional.of(new InventarioDomain()));

        when(ventaRepositoryPort.guardar(any(VentaDomain.class))).thenAnswer(invocation -> {
            VentaDomain v = invocation.getArgument(0);
            v.setId(100L);
            return v;
        });

        RegistrarVentaCommand command = new RegistrarVentaCommand(
                10L,
                1L,
                null,
                "Venta de prueba",
                List.of(new RegistrarVentaCommand.DetalleVentaCommand(5L, 2, new BigDecimal("10.00"))),
                List.of(new RegistrarVentaCommand.PagoVentaCommand(1L, new BigDecimal("20.00")))
        );

        VentaDomain resultado = registrarVentaUseCase.ejecutar(command);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals(new BigDecimal("20.00"), resultado.getTotal());
        assertEquals(18, productoStock.getStock()); // stock decrementado de 20 a 18
        verify(sesionCajaRepositoryPort).guardar(sesionAbierta);
        verify(movimientoCajaRepositoryPort).guardar(any(MovimientoCajaDomain.class));
        verify(inventarioRepositoryPort).registrarMovimiento(any(MovimientoInventarioDomain.class));
    }

    @Test
    @DisplayName("RegistrarVenta debe lanzar StockInsuficienteVentaException si el producto no tiene suficiente stock")
    void registrarVenta_StockInsuficiente_LanzaExcepcion() {
        when(sesionCajaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioActivo));
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(productoStock));

        RegistrarVentaCommand command = new RegistrarVentaCommand(
                10L,
                1L,
                null,
                "Sin stock suficiente",
                List.of(new RegistrarVentaCommand.DetalleVentaCommand(5L, 25, new BigDecimal("10.00"))), // stock es 20
                List.of(new RegistrarVentaCommand.PagoVentaCommand(1L, new BigDecimal("250.00")))
        );

        assertThrows(StockInsuficienteVentaException.class, () -> registrarVentaUseCase.ejecutar(command));
    }

    @Test
    @DisplayName("RegistrarVenta debe lanzar MontoPagoInsuficienteException si el pago no cubre el total")
    void registrarVenta_PagoInsuficiente_LanzaExcepcion() {
        when(sesionCajaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioActivo));
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(productoStock));
        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(metodoEfectivo));

        RegistrarVentaCommand command = new RegistrarVentaCommand(
                10L,
                1L,
                null,
                "Pago incompleto",
                List.of(new RegistrarVentaCommand.DetalleVentaCommand(5L, 2, new BigDecimal("10.00"))), // total = 20.00
                List.of(new RegistrarVentaCommand.PagoVentaCommand(1L, new BigDecimal("15.00")))  // paga 15.00
        );

        assertThrows(MontoPagoInsuficienteException.class, () -> registrarVentaUseCase.ejecutar(command));
    }

    @Test
    @DisplayName("AnularVenta debe reintegrar stock, ajustar efectivo de sesión y marcar venta como ANULADA")
    void anularVenta_Exitoso() {
        VentaDomain venta = VentaDomain.crear(10L, 1L, null, "Para anular");
        venta.setId(100L);
        venta.setCodigoFactura("VTA-20261009-00001");
        venta.agregarDetalle(new VentaDetalleDomain(5L, "Gaseosa Cola", 2, new BigDecimal("10.00")));
        venta.agregarPago(new PagoDomain(1L, "Efectivo", new BigDecimal("20.00")));

        when(ventaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(venta));
        when(productoRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(productoStock));
        when(sesionCajaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioActivo));
        when(inventarioRepositoryPort.buscarPorProductoId(5L)).thenReturn(Optional.of(new InventarioDomain()));

        when(ventaRepositoryPort.guardar(any(VentaDomain.class))).thenAnswer(i -> i.getArgument(0));

        VentaDomain anulada = anularVentaUseCase.ejecutar(100L, "Cliente canceló pedido");

        assertTrue(anulada.isAnulada());
        assertEquals(22, productoStock.getStock()); // Reintegró 2 unidades (20 + 2 = 22)
        verify(movimientoCajaRepositoryPort).guardar(any(MovimientoCajaDomain.class));
        verify(inventarioRepositoryPort).registrarMovimiento(any(MovimientoInventarioDomain.class));
    }

    @Test
    @DisplayName("ObtenerVentaPorId debe retornar venta si existe o lanzar VentaNoEncontradaException")
    void obtenerVentaPorId_ExistenteYNoExistente() {
        VentaDomain venta = VentaDomain.crear(10L, 1L, null, null);
        venta.setId(1L);

        when(ventaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(venta));
        when(ventaRepositoryPort.buscarPorId(999L)).thenReturn(Optional.empty());

        assertEquals(1L, obtenerVentaPorIdUseCase.ejecutar(1L).getId());
        assertThrows(VentaNoEncontradaException.class, () -> obtenerVentaPorIdUseCase.ejecutar(999L));
    }
}
