package com.example.pos.controller;

import com.example.pos.dto.ClienteRequestDto;
import com.example.pos.model.Cliente;
import com.example.pos.service.ClienteService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private Cliente cliente;
    private ClienteRequestDto dto;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Lucia Ramirez");
        cliente.setDocumento("87654321");

        dto = new ClienteRequestDto();
        dto.setNombre("Lucia Ramirez");
        dto.setDocumento("87654321");
    }

    @Test
    @DisplayName("GET /api/clientes debe retornar 200 y la lista de clientes")
    void listar_DebeRetornar200YLista() {
        when(clienteService.listarTodos()).thenReturn(List.of(cliente));

        ResponseEntity<List<Cliente>> response = clienteController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(clienteService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/clientes/{id} debe retornar 200 cuando existe")
    void buscarPorId_CuandoExiste_DebeRetornar200() {
        when(clienteService.buscarPorId(1)).thenReturn(Optional.of(cliente));

        ResponseEntity<Cliente> response = clienteController.buscarPorId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Lucia Ramirez", response.getBody().getNombre());
    }

    @Test
    @DisplayName("GET /api/clientes/{id} debe retornar 404 cuando no existe")
    void buscarPorId_CuandoNoExiste_DebeRetornar404() {
        when(clienteService.buscarPorId(99)).thenReturn(Optional.empty());

        ResponseEntity<Cliente> response = clienteController.buscarPorId(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /api/clientes debe retornar 201 CREATED al crear cliente exitosamente")
    void crear_Exitoso_DebeRetornar201() {
        when(clienteService.guardarCliente(any(ClienteRequestDto.class))).thenReturn(cliente);

        ResponseEntity<?> response = clienteController.crear(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cliente, response.getBody());
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} debe retornar 200 OK al actualizar cliente")
    void actualizar_Exitoso_DebeRetornar200() {
        when(clienteService.actualizarCliente(eq(1), any(ClienteRequestDto.class))).thenReturn(cliente);

        ResponseEntity<?> response = clienteController.actualizar(1, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cliente, response.getBody());
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} debe retornar 204 NO_CONTENT al eliminar cliente")
    void eliminar_DebeRetornar204() {
        doNothing().when(clienteService).eliminarCliente(1);

        ResponseEntity<Void> response = clienteController.eliminar(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(clienteService).eliminarCliente(1);
    }
}
