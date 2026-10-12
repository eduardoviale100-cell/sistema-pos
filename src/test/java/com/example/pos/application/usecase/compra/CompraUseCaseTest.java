package com.example.pos.application.usecase.compra;


import com.example.pos.domain.model.usuario.UsuarioDomain;
import com.example.pos.domain.model.producto.ProductoDomain;
import com.example.pos.domain.model.proveedor.ProveedorDomain;
import com.example.pos.domain.model.usuario.RolUsuario;
import com.example.pos.domain.exception.CompraNoEncontradaException;
import com.example.pos.domain.model.compra.*;
import com.example.pos.domain.model.caja.*;
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
class CompraUseCaseTest {

    @Mock
    private CompraRepositoryPort compraRepositoryPort;

    @Mock
    private ProveedorRepositoryPort proveedorRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private ProductoRepositoryPort productoRepositoryPort;

    @Mock
    private InventarioRepositoryPort inventarioRepositoryPort;

    @InjectMocks
    private RegistrarCompraUseCase registrarCompraUseCase;

    private ListarComprasUseCase listarComprasUseCase;
    private ObtenerCompraPorIdUseCase obtenerCompraPorIdUseCase;

    private ProveedorDomain proveedor;
    private ProductoDomain producto;
    private UsuarioDomain usuario;

    @BeforeEach
    void setUp() {
        listarComprasUseCase = new ListarComprasUseCase(compraRepositoryPort);
        obtenerCompraPorIdUseCase = new ObtenerCompraPorIdUseCase(compraRepositoryPort);

        proveedor = new ProveedorDomain(10L, "20123456789", "Distribuidora Lima", "999888777", "contacto@lima.com", "Av. Principal 123");
        producto = new ProductoDomain(1L, "PROD-1", "Aceite", null, new BigDecimal("8.00"), 10, true);
        usuario = new UsuarioDomain(2L, "Admin", "admin", "hash", RolUsuario.ADMIN, true, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("RegistrarCompra debe aumentar stock, actualizar costo del producto y guardar compra")
    void registrarCompra_Exitoso() {
        when(proveedorRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(proveedor));
        when(usuarioRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(usuario));
        when(productoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(producto));
        when(inventarioRepositoryPort.buscarPorProductoId(1L)).thenReturn(Optional.empty());
        when(compraRepositoryPort.guardar(any())).thenAnswer(i -> i.getArgument(0));

        RegistrarCompraCommand command = new RegistrarCompraCommand(
                10L,
                2L,
                "Factura F001-123",
                List.of(new RegistrarCompraCommand.DetalleCompraCommand(1L, 10, new BigDecimal("6.50")))
        );

        CompraDomain resultado = registrarCompraUseCase.ejecutar(command);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("65.00"), resultado.getTotal());
        assertEquals(20, producto.getStock()); // 10 + 10 = 20
        verify(inventarioRepositoryPort).registrarMovimiento(any());
    }

    @Test
    @DisplayName("ObtenerCompraPorId debe retornar la compra si existe o lanzar CompraNoEncontradaException")
    void obtenerCompraPorId_ExistenteYNoExistente() {
        CompraDomain compra = CompraDomain.crear(10L, 2L, null);
        compra.setId(100L);

        when(compraRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(compra));
        when(compraRepositoryPort.buscarPorId(999L)).thenReturn(Optional.empty());

        assertEquals(100L, obtenerCompraPorIdUseCase.ejecutar(100L).getId());
        assertThrows(CompraNoEncontradaException.class, () -> obtenerCompraPorIdUseCase.ejecutar(999L));
    }
}

