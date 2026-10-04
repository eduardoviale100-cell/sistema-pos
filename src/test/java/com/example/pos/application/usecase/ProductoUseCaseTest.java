package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.exception.ProductoYaExisteException;
import com.example.pos.domain.model.ProductoDomain;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoUseCaseTest {

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    private CrearProductoUseCase crearProductoUseCase;
    private ActualizarProductoUseCase actualizarProductoUseCase;
    private DesactivarProductoUseCase desactivarProductoUseCase;
    private ListarProductosUseCase listarProductosUseCase;
    private BuscarProductoPorIdUseCase buscarProductoPorIdUseCase;

    @BeforeEach
    void setUp() {
        crearProductoUseCase = new CrearProductoUseCase(productoRepositoryPort);
        actualizarProductoUseCase = new ActualizarProductoUseCase(productoRepositoryPort);
        desactivarProductoUseCase = new DesactivarProductoUseCase(productoRepositoryPort);
        listarProductosUseCase = new ListarProductosUseCase(productoRepositoryPort);
        buscarProductoPorIdUseCase = new BuscarProductoPorIdUseCase(productoRepositoryPort);
    }

    @Test
    @DisplayName("Debe crear producto exitosamente cuando el código no existe")
    void crearProducto_Exitoso() {
        CrearProductoCommand command = new CrearProductoCommand("7751234567890", "Laptop Pro", "Laptop Gamer", new BigDecimal("3500.00"));

        when(productoRepositoryPort.existePorCodigo("7751234567890")).thenReturn(false);
        when(productoRepositoryPort.guardar(any(ProductoDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductoDomain resultado = crearProductoUseCase.ejecutar(command);

        assertNotNull(resultado);
        assertEquals("Laptop Pro", resultado.getNombre());
        assertEquals("7751234567890", resultado.getCodigo());
        verify(productoRepositoryPort, times(1)).guardar(any(ProductoDomain.class));
    }

    @Test
    @DisplayName("Debe lanzar ProductoYaExisteException al crear producto con código duplicado")
    void crearProducto_CodigoDuplicado_LanzaExcepcion() {
        CrearProductoCommand command = new CrearProductoCommand("7751234567890", "Laptop Pro", "Laptop Gamer", new BigDecimal("3500.00"));

        when(productoRepositoryPort.existePorCodigo("7751234567890")).thenReturn(true);

        assertThrows(ProductoYaExisteException.class, () -> crearProductoUseCase.ejecutar(command));
        verify(productoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe actualizar producto existente")
    void actualizarProducto_Exitoso() {
        ProductoDomain producto = new ProductoDomain(1L, "COD-1", "Original", "Desc", new BigDecimal("10.00"), 5, true);
        ActualizarProductoCommand command = new ActualizarProductoCommand("Modificado", "Nueva desc", new BigDecimal("15.00"), 10, true);

        when(productoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(ProductoDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductoDomain actualizado = actualizarProductoUseCase.ejecutar(1L, command);

        assertEquals("Modificado", actualizado.getNombre());
        assertEquals("Nueva desc", actualizado.getDescripcion());
        assertEquals(new BigDecimal("15.00"), actualizado.getPrecioVenta());
        assertEquals(10, actualizado.getStock());
    }

    @Test
    @DisplayName("Debe desactivar producto exitosamente")
    void desactivarProducto_Exitoso() {
        ProductoDomain producto = new ProductoDomain(1L, "COD-1", "Original", "Desc", new BigDecimal("10.00"), 5, true);

        when(productoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(producto));
        when(productoRepositoryPort.guardar(any(ProductoDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductoDomain desactivado = desactivarProductoUseCase.ejecutar(1L);

        assertFalse(desactivado.getEstado());
        verify(productoRepositoryPort, times(1)).guardar(producto);
    }

    @Test
    @DisplayName("Debe buscar producto por ID o lanzar ProductoNoEncontradoException")
    void buscarPorId_CuandoNoExiste_LanzaExcepcion() {
        when(productoRepositoryPort.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontradoException.class, () -> buscarProductoPorIdUseCase.ejecutar(999L));
    }

    @Test
    @DisplayName("Debe listar todos los productos")
    void listarTodos_RetornaLista() {
        when(productoRepositoryPort.listarTodos()).thenReturn(List.of(
                new ProductoDomain(1L, "C1", "P1", "", BigDecimal.TEN, 2, true)
        ));

        List<ProductoDomain> resultado = listarProductosUseCase.ejecutar();

        assertEquals(1, resultado.size());
        verify(productoRepositoryPort, times(1)).listarTodos();
    }
}
