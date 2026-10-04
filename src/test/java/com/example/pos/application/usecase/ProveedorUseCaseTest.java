package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProveedorDuplicadoException;
import com.example.pos.domain.exception.ProveedorNoEncontradoException;
import com.example.pos.domain.model.ProveedorDomain;
import com.example.pos.domain.repository.ProveedorRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorUseCaseTest {

    @Mock
    private ProveedorRepositoryPort proveedorRepositoryPort;

    private CrearProveedorUseCase crearProveedorUseCase;
    private ActualizarProveedorUseCase actualizarProveedorUseCase;
    private ListarProveedoresUseCase listarProveedoresUseCase;
    private BuscarProveedorPorIdUseCase buscarProveedorPorIdUseCase;
    private EliminarProveedorUseCase eliminarProveedorUseCase;

    @BeforeEach
    void setUp() {
        crearProveedorUseCase = new CrearProveedorUseCase(proveedorRepositoryPort);
        actualizarProveedorUseCase = new ActualizarProveedorUseCase(proveedorRepositoryPort);
        listarProveedoresUseCase = new ListarProveedoresUseCase(proveedorRepositoryPort);
        buscarProveedorPorIdUseCase = new BuscarProveedorPorIdUseCase(proveedorRepositoryPort);
        eliminarProveedorUseCase = new EliminarProveedorUseCase(proveedorRepositoryPort);
    }

    @Test
    @DisplayName("Debe crear un proveedor exitosamente cuando el RUC no existe")
    void crearProveedor_Exitoso() {
        CrearProveedorCommand command = new CrearProveedorCommand(
                "Distribuidora Lima S.A.C.", "20123456789", "987654321", "contacto@distlima.com", "Av. Principal 123"
        );

        when(proveedorRepositoryPort.existePorRucDni("20123456789")).thenReturn(false);
        when(proveedorRepositoryPort.guardar(any(ProveedorDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProveedorDomain resultado = crearProveedorUseCase.ejecutar(command);

        assertNotNull(resultado);
        assertEquals("Distribuidora Lima S.A.C.", resultado.getNombre());
        assertEquals("20123456789", resultado.getRucDni());
        verify(proveedorRepositoryPort, times(1)).guardar(any(ProveedorDomain.class));
    }

    @Test
    @DisplayName("Debe lanzar ProveedorDuplicadoException cuando el RUC ya está registrado")
    void crearProveedor_RucDuplicado_LanzaExcepcion() {
        CrearProveedorCommand command = new CrearProveedorCommand(
                "Distribuidora Lima S.A.C.", "20123456789", "987654321", "contacto@distlima.com", "Av. Principal 123"
        );

        when(proveedorRepositoryPort.existePorRucDni("20123456789")).thenReturn(true);

        assertThrows(ProveedorDuplicadoException.class, () -> crearProveedorUseCase.ejecutar(command));
        verify(proveedorRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe actualizar un proveedor existente")
    void actualizarProveedor_Exitoso() {
        ProveedorDomain proveedor = new ProveedorDomain(1L, "Original S.A.", "20123456789", "987654321", "orig@mail.com", "Dir 1");
        ActualizarProveedorCommand command = new ActualizarProveedorCommand(
                "Actualizado S.A.", "999888777", "nuevo@mail.com", "Nueva Dir 456"
        );

        when(proveedorRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepositoryPort.guardar(any(ProveedorDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProveedorDomain actualizado = actualizarProveedorUseCase.ejecutar(1L, command);

        assertEquals("Actualizado S.A.", actualizado.getNombre());
        assertEquals("999888777", actualizado.getTelefono());
        assertEquals("nuevo@mail.com", actualizado.getEmail());
        assertEquals("Nueva Dir 456", actualizado.getDireccion());
        assertEquals("20123456789", actualizado.getRucDni()); // Inmutable
    }

    @Test
    @DisplayName("Debe listar todos los proveedores")
    void listarProveedores_RetornaLista() {
        when(proveedorRepositoryPort.listarTodos()).thenReturn(List.of(
                new ProveedorDomain(1L, "Prov 1", "20100000001", "987654321", "p1@mail.com", "Dir 1")
        ));

        List<ProveedorDomain> resultado = listarProveedoresUseCase.ejecutar();

        assertEquals(1, resultado.size());
        verify(proveedorRepositoryPort, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Debe buscar proveedor por ID o lanzar ProveedorNoEncontradoException")
    void buscarPorId_NoExiste_LanzaExcepcion() {
        when(proveedorRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ProveedorNoEncontradoException.class, () -> buscarProveedorPorIdUseCase.ejecutar(99L));
    }

    @Test
    @DisplayName("Debe eliminar proveedor si existe")
    void eliminarProveedor_Exitoso() {
        when(proveedorRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(
                new ProveedorDomain(1L, "Prov 1", "20100000001", "987654321", "p1@mail.com", "Dir 1")
        ));

        eliminarProveedorUseCase.ejecutar(1L);

        verify(proveedorRepositoryPort, times(1)).eliminarPorId(1L);
    }
}
