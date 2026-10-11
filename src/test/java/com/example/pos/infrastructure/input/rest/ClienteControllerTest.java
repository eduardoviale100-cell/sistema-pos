package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.*;
import com.example.pos.domain.model.ClienteDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private CrearClienteUseCase crearClienteUseCase;

    @Mock
    private ActualizarClienteUseCase actualizarClienteUseCase;

    @Mock
    private DesactivarClienteUseCase desactivarClienteUseCase;

    @Mock
    private ListarClientesUseCase listarClientesUseCase;

    @Mock
    private BuscarClientePorIdUseCase buscarClientePorIdUseCase;

    @InjectMocks
    private ClienteController clienteController;

    private ClienteDomain cliente;

    @BeforeEach
    void setUp() {
        cliente = new ClienteDomain(1L, "Lucia Ramirez", "87654321", "987654321", "lucia@test.com", "Av Lima", true);
    }

    @Test
    @DisplayName("GET /api/clientes debe retornar 200 y la lista de clientes")
    void listarTodos_DebeRetornar200YLista() {
        when(listarClientesUseCase.ejecutar()).thenReturn(List.of(cliente));

        ResponseEntity<List<ClienteDomain>> response = clienteController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(listarClientesUseCase).ejecutar();
    }

    @Test
    @DisplayName("GET /api/clientes/{id} debe retornar 200 cuando existe")
    void buscarPorId_DebeRetornar200() {
        when(buscarClientePorIdUseCase.ejecutar(1L)).thenReturn(cliente);

        ResponseEntity<ClienteDomain> response = clienteController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(buscarClientePorIdUseCase).ejecutar(1L);
    }

    @Test
    @DisplayName("POST /api/clientes debe retornar 201 CREATED")
    void crear_DebeRetornar201() {
        ClienteRequestDto dto = new ClienteRequestDto();
        dto.setNombre("Lucia Ramirez");
        dto.setDocumento("87654321");

        when(crearClienteUseCase.ejecutar(any(CrearClienteCommand.class))).thenReturn(cliente);

        ResponseEntity<ClienteDomain> response = clienteController.crear(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Lucia Ramirez", response.getBody().getNombre());
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} debe retornar 200 OK con mensaje de confirmación")
    void desactivar_DebeRetornar200() {
        cliente.setEstado(false);
        when(desactivarClienteUseCase.ejecutar(1L)).thenReturn(cliente);

        ResponseEntity<java.util.Map<String, Object>> response = clienteController.desactivar(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El cliente ha sido desactivado exitosamente.", response.getBody().get("mensaje"));
        verify(desactivarClienteUseCase).ejecutar(1L);
    }
}
