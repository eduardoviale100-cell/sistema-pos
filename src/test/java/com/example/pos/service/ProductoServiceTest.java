package com.example.pos.service;

import com.example.pos.dto.ProductoRequestDto;
import com.example.pos.model.*;
import com.example.pos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private InventarioRepository inventarioRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private MarcaRepository marcaRepository;
    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;
    private Categoria categoria;
    private Marca marca;
    private Proveedor proveedor;
    private ProductoRequestDto dto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Bebidas");

        marca = new Marca();
        marca.setId(2);
        marca.setNombre("Coca-Cola");

        proveedor = new Proveedor();
        proveedor.setId(3);
        proveedor.setNombre("Distribuidora Central");

        producto = new Producto();
        producto.setId(10);
        producto.setNombre("Gaseosa 500ml");
        producto.setCodigoBarras("7751234567890");
        producto.setPrecioCompra(new BigDecimal("1.50"));
        producto.setPrecioVenta(new BigDecimal("2.50"));
        producto.setCategoria(categoria);
        producto.setMarca(marca);
        producto.setProveedor(proveedor);

        dto = new ProductoRequestDto();
        dto.setNombre("Gaseosa 500ml");
        dto.setCodigoBarras("7751234567890");
        dto.setPrecioCompra(new BigDecimal("1.50"));
        dto.setPrecioVenta(new BigDecimal("2.50"));
        dto.setCategoriaId(1);
        dto.setMarcaId(2);
        dto.setProveedorId(3);
    }

    @Test
    @DisplayName("Debe listar todos los productos")
    void listarTodos_DebeRetornarLista() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = productoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository).findAll();
    }

    @Test
    @DisplayName("Debe buscar producto por ID")
    void buscarPorId_CuandoExiste_DebeRetornarProducto() {
        when(productoRepository.findById(10)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.buscarPorId(10);

        assertTrue(resultado.isPresent());
        assertEquals("Gaseosa 500ml", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Debe guardar producto y crear automáticamente el registro de inventario inicial")
    void guardarProducto_DebeGuardarYCrearInventario() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(marcaRepository.findById(2)).thenReturn(Optional.of(marca));
        when(proveedorRepository.findById(3)).thenReturn(Optional.of(proveedor));

        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> {
            Producto p = i.getArgument(0);
            p.setId(10);
            return p;
        });

        Producto guardado = productoService.guardarProducto(dto);

        assertNotNull(guardado);
        assertEquals(10, guardado.getId());
        assertEquals("Gaseosa 500ml", guardado.getNombre());
        assertEquals(categoria, guardado.getCategoria());
        assertEquals(marca, guardado.getMarca());
        assertEquals(proveedor, guardado.getProveedor());

        // Verificar que se crea el inventario con stock 0 y stock mínimo 5
        ArgumentCaptor<Inventario> inventarioCaptor = ArgumentCaptor.forClass(Inventario.class);
        verify(inventarioRepository).save(inventarioCaptor.capture());
        Inventario inventarioCreado = inventarioCaptor.getValue();
        assertEquals(guardado, inventarioCreado.getProducto());
        assertEquals(0, inventarioCreado.getStockActual());
        assertEquals(5, inventarioCreado.getStockMinimo());
    }

    @Test
    @DisplayName("Debe actualizar producto existente y sus relaciones")
    void actualizarProducto_DebeActualizarCampos() {
        when(productoRepository.findById(10)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(marcaRepository.findById(2)).thenReturn(Optional.of(marca));
        when(proveedorRepository.findById(3)).thenReturn(Optional.of(proveedor));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        dto.setNombre("Gaseosa 500ml Zero");
        dto.setPrecioVenta(new BigDecimal("3.00"));

        Producto actualizado = productoService.actualizarProducto(10, dto);

        assertNotNull(actualizado);
        assertEquals("Gaseosa 500ml Zero", actualizado.getNombre());
        assertEquals(new BigDecimal("3.00"), actualizado.getPrecioVenta());
        verify(productoRepository).save(producto);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar producto inexistente")
    void actualizarProducto_NoExiste_LanzaExcepcion() {
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productoService.actualizarProducto(99, dto));
        assertTrue(ex.getMessage().contains("Producto no encontrado con ID: 99"));
    }

    @Test
    @DisplayName("Debe eliminar producto por ID")
    void eliminarProducto_DebeInvocarDeleteById() {
        doNothing().when(productoRepository).deleteById(10);

        assertDoesNotThrow(() -> productoService.eliminarProducto(10));
        verify(productoRepository).deleteById(10);
    }
}
