package com.example.pos.service;

import com.example.pos.dto.CompraDetalleRequestDto;
import com.example.pos.dto.CompraRequestDto;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;
    @Mock
    private ProveedorRepository proveedorRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @InjectMocks
    private CompraService compraService;

    private Proveedor proveedor;
    private Usuario usuario;
    private Producto producto;
    private Inventario inventario;
    private CompraRequestDto compraDto;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1);
        proveedor.setNombre("Proveedor Mayorista");

        usuario = new Usuario();
        usuario.setId(2);
        usuario.setNombre("Gerente Compras");

        producto = new Producto();
        producto.setId(10);
        producto.setNombre("Teclado Mecanico");
        producto.setPrecioCompra(new BigDecimal("25.00"));

        inventario = new Inventario();
        inventario.setId(5);
        inventario.setProducto(producto);
        inventario.setStockActual(10);

        compraDto = new CompraRequestDto();
        compraDto.setProveedorId(1);
        compraDto.setUsuarioId(2);
        compraDto.setObservaciones("Abastecimiento mensual");

        CompraDetalleRequestDto detDto = new CompraDetalleRequestDto();
        detDto.setProductoId(10);
        detDto.setCantidad(20);
        detDto.setPrecioUnitario(new BigDecimal("30.00"));

        compraDto.setDetalles(List.of(detDto));
    }

    @Test
    @DisplayName("Debe registrar una compra aumentando el stock y actualizando el precio de compra del producto")
    void registrarCompra_Exitosa() {
        when(proveedorRepository.findById(1)).thenReturn(Optional.of(proveedor));
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(10)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoId(10)).thenReturn(Optional.of(inventario));

        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> {
            Compra c = i.getArgument(0);
            if (c.getId() == null) {
                c.setId(100);
            }
            return c;
        });

        Compra compra = compraService.registrarCompra(compraDto);

        assertNotNull(compra);
        assertEquals(new BigDecimal("600.00"), compra.getTotal()); // 20 * 30.00
        assertEquals(proveedor, compra.getProveedor());
        assertEquals(usuario, compra.getUsuario());

        // Verificar incremento de stock de 10 a 30
        assertEquals(30, inventario.getStockActual());
        verify(inventarioRepository).save(inventario);

        // Verificar actualización de precioCompra a 30.00
        assertEquals(new BigDecimal("30.00"), producto.getPrecioCompra());
        verify(productoRepository).save(producto);

        // Verificar registro de movimiento ENTRADA
        verify(movimientoInventarioRepository).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el proveedor no existe")
    void registrarCompra_ProveedorNoExiste_LanzaExcepcion() {
        when(proveedorRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> compraService.registrarCompra(compraDto));
        assertTrue(ex.getMessage().contains("Proveedor no encontrado con ID: 1"));
        verify(compraRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe listar compras")
    void listarCompras_DebeRetornarLista() {
        when(compraRepository.findAllWithDetails()).thenReturn(List.of());

        List<Compra> resultado = compraService.listarCompras();

        assertNotNull(resultado);
        verify(compraRepository).findAllWithDetails();
    }

    @Test
    @DisplayName("Debe buscar compra por ID")
    void buscarPorId_CuandoExiste_DebeRetornarCompra() {
        Compra c = new Compra();
        c.setId(100);
        when(compraRepository.findByIdWithDetails(100)).thenReturn(Optional.of(c));

        Optional<Compra> resultado = compraService.buscarPorId(100);

        assertTrue(resultado.isPresent());
        assertEquals(100, resultado.get().getId());
    }
}
