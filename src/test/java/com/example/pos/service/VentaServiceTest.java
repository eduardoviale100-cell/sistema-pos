package com.example.pos.service;

import com.example.pos.dto.PagoRequestDto;
import com.example.pos.dto.VentaDetalleRequestDto;
import com.example.pos.dto.VentaRequestDto;
import com.example.pos.model.*;
import com.example.pos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private SesionCajaRepository sesionCajaRepository;
    @Mock
    private MetodoPagoRepository metodoPagoRepository;
    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @InjectMocks
    private VentaService ventaService;

    private SesionCaja sesionAbierta;
    private Usuario usuarioActivo;
    private Cliente cliente;
    private Producto producto;
    private Inventario inventario;
    private MetodoPago metodoEfectivo;
    private MetodoPago metodoTarjeta;
    private VentaRequestDto ventaDto;

    @BeforeEach
    void setUp() {
        Caja caja = new Caja();
        caja.setId(1);
        caja.setNombre("Caja Principal");
        caja.setEstado("ABIERTA");

        usuarioActivo = new Usuario();
        usuarioActivo.setId(1);
        usuarioActivo.setNombre("Carlos Vendedor");
        usuarioActivo.setUsuario("cvendedor");
        usuarioActivo.setActivo(true);

        sesionAbierta = new SesionCaja();
        sesionAbierta.setId(10);
        sesionAbierta.setCaja(caja);
        sesionAbierta.setUsuario(usuarioActivo);
        sesionAbierta.setEstado("ABIERTA");
        sesionAbierta.setMontoInicial(new BigDecimal("100.00"));
        sesionAbierta.setMontoEsperado(new BigDecimal("100.00"));

        cliente = new Cliente();
        cliente.setId(5);
        cliente.setNombre("Ana Lopez");

        producto = new Producto();
        producto.setId(20);
        producto.setNombre("Laptop Gamer");
        producto.setPrecioVenta(new BigDecimal("1200.00"));

        inventario = new Inventario();
        inventario.setId(30);
        inventario.setProducto(producto);
        inventario.setStockActual(10);
        inventario.setStockMinimo(2);

        metodoEfectivo = new MetodoPago();
        metodoEfectivo.setId(1);
        metodoEfectivo.setNombre("Efectivo");
        metodoEfectivo.setActivo(true);

        metodoTarjeta = new MetodoPago();
        metodoTarjeta.setId(2);
        metodoTarjeta.setNombre("Tarjeta de Débito");
        metodoTarjeta.setActivo(true);

        // Armar DTO de venta estándar
        ventaDto = new VentaRequestDto();
        ventaDto.setSesionCajaId(10);
        ventaDto.setUsuarioId(1);
        ventaDto.setClienteId(5);
        ventaDto.setObservaciones("Venta mostrador normal");

        VentaDetalleRequestDto detalleDto = new VentaDetalleRequestDto();
        detalleDto.setProductoId(20);
        detalleDto.setCantidad(2);
        detalleDto.setPrecioUnitario(new BigDecimal("1200.00"));
        ventaDto.setDetalles(List.of(detalleDto));

        PagoRequestDto pagoEfectivo = new PagoRequestDto();
        pagoEfectivo.setMetodoPagoId(1);
        pagoEfectivo.setMonto(new BigDecimal("1000.00"));

        PagoRequestDto pagoTarjeta = new PagoRequestDto();
        pagoTarjeta.setMetodoPagoId(2);
        pagoTarjeta.setMonto(new BigDecimal("1400.00"));

        ventaDto.setPagos(List.of(pagoEfectivo, pagoTarjeta));
    }

    @Test
    @DisplayName("Debe registrar una venta exitosa con descuento de inventario y actualización de caja")
    void registrarVenta_Exitosa() {
        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(20)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoId(20)).thenReturn(Optional.of(inventario));
        when(metodoPagoRepository.findById(1)).thenReturn(Optional.of(metodoEfectivo));
        when(metodoPagoRepository.findById(2)).thenReturn(Optional.of(metodoTarjeta));
        when(ventaRepository.count()).thenReturn(5L);

        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> {
            Venta v = invocation.getArgument(0);
            v.setId(999);
            return v;
        });

        Venta resultado = ventaService.registrarVenta(ventaDto);

        assertNotNull(resultado);
        assertEquals(999, resultado.getId());
        assertEquals(new BigDecimal("2400.00"), resultado.getTotal());
        assertTrue(resultado.getCodigoFactura().startsWith("VTA-"));
        assertTrue(resultado.getCodigoFactura().endsWith("-00006"));

        // Verificar descuento de inventario: de 10 a 8
        assertEquals(8, inventario.getStockActual());
        verify(inventarioRepository, times(1)).save(inventario);

        // Verificar aumento del monto esperado de la caja por pago en efectivo (+1000.00)
        assertEquals(new BigDecimal("1100.00"), sesionAbierta.getMontoEsperado());
        verify(sesionCajaRepository, times(1)).save(sesionAbierta);

        // Verificar registro del movimiento de inventario SALIDA
        verify(movimientoInventarioRepository, times(1)).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException si la sesión de caja no existe")
    void registrarVenta_SesionNoExiste_LanzaExcepcion() {
        when(sesionCajaRepository.findById(10)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> ventaService.registrarVenta(ventaDto));
        assertTrue(ex.getMessage().contains("Sesión de caja no encontrada"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si la sesión de caja está CERRADA")
    void registrarVenta_SesionCerrada_LanzaExcepcion() {
        sesionAbierta.setEstado("CERRADA");
        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> ventaService.registrarVenta(ventaDto));
        assertTrue(ex.getMessage().contains("no está ABIERTA"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si el usuario está inactivo")
    void registrarVenta_UsuarioInactivo_LanzaExcepcion() {
        usuarioActivo.setActivo(false);
        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> ventaService.registrarVenta(ventaDto));
        assertTrue(ex.getMessage().contains("no se encuentra activo"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe registrar venta sin cliente cuando clienteId es nulo")
    void registrarVenta_ClienteNulo_RegistraExitoso() {
        ventaDto.setClienteId(null);
        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));
        when(productoRepository.findById(20)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoId(20)).thenReturn(Optional.of(inventario));
        when(metodoPagoRepository.findById(1)).thenReturn(Optional.of(metodoEfectivo));
        when(metodoPagoRepository.findById(2)).thenReturn(Optional.of(metodoTarjeta));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta resultado = ventaService.registrarVenta(ventaDto);

        assertNotNull(resultado);
        assertNull(resultado.getCliente());
        verify(clienteRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si el stock es insuficiente")
    void registrarVenta_StockInsuficiente_LanzaExcepcion() {
        inventario.setStockActual(1); // Solicitado es 2
        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(20)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoId(20)).thenReturn(Optional.of(inventario));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> ventaService.registrarVenta(ventaDto));
        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si un método de pago no está activo")
    void registrarVenta_MetodoPagoInactivo_LanzaExcepcion() {
        metodoTarjeta.setActivo(false);
        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(20)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoId(20)).thenReturn(Optional.of(inventario));
        when(metodoPagoRepository.findById(1)).thenReturn(Optional.of(metodoEfectivo));
        when(metodoPagoRepository.findById(2)).thenReturn(Optional.of(metodoTarjeta));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> ventaService.registrarVenta(ventaDto));
        assertTrue(ex.getMessage().contains("no está activo"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el total pagado es menor al total calculado")
    void registrarVenta_PagoInsuficiente_LanzaExcepcion() {
        // Total venta: 2 * 1200 = 2400. Pagos suman solo 1500
        PagoRequestDto pagoInsuficiente = new PagoRequestDto();
        pagoInsuficiente.setMetodoPagoId(1);
        pagoInsuficiente.setMonto(new BigDecimal("1500.00"));
        ventaDto.setPagos(List.of(pagoInsuficiente));

        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));
        when(clienteRepository.findById(5)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(20)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoId(20)).thenReturn(Optional.of(inventario));
        when(metodoPagoRepository.findById(1)).thenReturn(Optional.of(metodoEfectivo));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> ventaService.registrarVenta(ventaDto));
        assertTrue(ex.getMessage().contains("Monto de pago insuficiente"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe listar todas las ventas")
    void listarVentas_DebeRetornarLista() {
        Venta v = new Venta();
        v.setId(1);
        v.setDetalles(new ArrayList<>());
        v.setPagos(new ArrayList<>());
        when(ventaRepository.findAllWithDetails()).thenReturn(List.of(v));

        List<Venta> resultado = ventaService.listarVentas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findAllWithDetails();
    }

    @Test
    @DisplayName("Debe listar ventas por usuario")
    void listarPorUsuario_DebeRetornarLista() {
        when(ventaRepository.findByUsuarioId(1)).thenReturn(List.of());

        List<Venta> resultado = ventaService.listarPorUsuario(1);

        assertNotNull(resultado);
        verify(ventaRepository, times(1)).findByUsuarioId(1);
    }

    @Test
    @DisplayName("Debe listar ventas por sesión de caja")
    void listarPorSesionCaja_DebeRetornarLista() {
        when(ventaRepository.findBySesionCajaIdWithDetails(10)).thenReturn(List.of());

        List<Venta> resultado = ventaService.listarPorSesionCaja(10);

        assertNotNull(resultado);
        verify(ventaRepository, times(1)).findBySesionCajaIdWithDetails(10);
    }

    @Test
    @DisplayName("Debe buscar venta por ID")
    void buscarPorId_CuandoExiste_DebeRetornarVenta() {
        Venta v = new Venta();
        v.setId(100);
        v.setPagos(new ArrayList<>());
        when(ventaRepository.findByIdWithDetails(100)).thenReturn(Optional.of(v));

        Optional<Venta> resultado = ventaService.buscarPorId(100);

        assertTrue(resultado.isPresent());
        assertEquals(100, resultado.get().getId());
        verify(ventaRepository, times(1)).findByIdWithDetails(100);
    }
}
