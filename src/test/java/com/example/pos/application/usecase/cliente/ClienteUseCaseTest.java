package com.example.pos.application.usecase.cliente;

import com.example.pos.domain.exception.ClienteDuplicadoException;
import com.example.pos.domain.exception.ClienteNoEncontradoException;
import com.example.pos.domain.model.cliente.ClienteDomain;
import com.example.pos.domain.repository.ClienteRepositoryPort;
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
class ClienteUseCaseTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    private CrearClienteUseCase crearClienteUseCase;
    private ActualizarClienteUseCase actualizarClienteUseCase;
    private BuscarClientePorIdUseCase buscarClientePorIdUseCase;
    private ListarClientesUseCase listarClientesUseCase;
    private DesactivarClienteUseCase desactivarClienteUseCase;

    private ClienteDomain cliente;

    @BeforeEach
    void setUp() {
        crearClienteUseCase = new CrearClienteUseCase(clienteRepositoryPort);
        actualizarClienteUseCase = new ActualizarClienteUseCase(clienteRepositoryPort);
        buscarClientePorIdUseCase = new BuscarClientePorIdUseCase(clienteRepositoryPort);
        listarClientesUseCase = new ListarClientesUseCase(clienteRepositoryPort);
        desactivarClienteUseCase = new DesactivarClienteUseCase(clienteRepositoryPort);

        cliente = new ClienteDomain(1L, "Juan Perez", "12345678", "987654321", "juan@example.com", "Av 123", true);
    }

    @Test
    @DisplayName("Crear cliente exitosamente")
    void crearCliente_Exitoso() {
        when(clienteRepositoryPort.existePorRucDni("12345678")).thenReturn(false);
        when(clienteRepositoryPort.guardar(any(ClienteDomain.class))).thenAnswer(i -> i.getArgument(0));

        ClienteDomain resultado = crearClienteUseCase.ejecutar("Juan Perez", "12345678", "987654321", "juan@example.com", "Av 123");

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("12345678", resultado.getRucDni());
        verify(clienteRepositoryPort).guardar(any(ClienteDomain.class));
    }

    @Test
    @DisplayName("Lanzar ClienteDuplicadoException si RUC/DNI ya existe")
    void crearCliente_Duplicado_LanzaExcepcion() {
        when(clienteRepositoryPort.existePorRucDni("12345678")).thenReturn(true);

        assertThrows(ClienteDuplicadoException.class, () ->
                crearClienteUseCase.ejecutar("Juan Perez", "12345678", "987654321", "juan@example.com", "Av 123")
        );
    }

    @Test
    @DisplayName("Buscar cliente por ID exitosamente")
    void buscarPorId_Exitoso() {
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        ClienteDomain resultado = buscarClientePorIdUseCase.ejecutar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Lanzar ClienteNoEncontradoException al buscar ID inexistente")
    void buscarPorId_NoExiste_LanzaExcepcion() {
        when(clienteRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class, () ->
                buscarClientePorIdUseCase.ejecutar(99L)
        );
    }

    @Test
    @DisplayName("Listar clientes exitosamente")
    void listarClientes_Exitoso() {
        when(clienteRepositoryPort.listarTodos()).thenReturn(List.of(cliente));

        List<ClienteDomain> lista = listarClientesUseCase.ejecutar();

        assertNotNull(lista);
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Desactivar cliente exitosamente")
    void desactivarCliente_Exitoso() {
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepositoryPort.guardar(any(ClienteDomain.class))).thenAnswer(i -> i.getArgument(0));

        desactivarClienteUseCase.ejecutar(1L);

        assertFalse(cliente.isActivo());
        verify(clienteRepositoryPort).guardar(cliente);
    }
}

