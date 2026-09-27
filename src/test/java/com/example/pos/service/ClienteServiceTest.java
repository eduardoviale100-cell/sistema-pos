package com.example.pos.service;

import com.example.pos.dto.ClienteRequestDto;
import com.example.pos.model.Cliente;
import com.example.pos.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteRequestDto clienteDto;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Juan Perez");
        cliente.setDocumento("12345678");
        cliente.setTelefono("987654321");
        cliente.setEmail("juan@example.com");
        cliente.setDireccion("Av. Principal 123");

        clienteDto = new ClienteRequestDto();
        clienteDto.setNombre("Juan Perez");
        clienteDto.setDocumento("12345678");
        clienteDto.setTelefono("987654321");
        clienteDto.setEmail("juan@example.com");
        clienteDto.setDireccion("Av. Principal 123");
    }

    @Test
    @DisplayName("Debe listar todos los clientes correctamente")
    void listarTodos_DebeRetornarListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente));

        List<Cliente> resultado = clienteService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un cliente por ID cuando existe")
    void buscarPorId_CuandoExiste_DebeRetornarCliente() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Juan Perez", resultado.get().getNombre());
        verify(clienteRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar vacío al buscar por ID inexistente")
    void buscarPorId_CuandoNoExiste_DebeRetornarVacio() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.buscarPorId(99);

        assertFalse(resultado.isPresent());
        verify(clienteRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe guardar un nuevo cliente correctamente con datos limpios")
    void guardarCliente_DebeGuardarYRetornarCliente() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            c.setId(1);
            return c;
        });

        Cliente resultado = clienteService.guardarCliente(clienteDto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("12345678", resultado.getDocumento());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe actualizar un cliente existente")
    void actualizarCliente_CuandoExiste_DebeActualizarCampos() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        clienteDto.setNombre("Juan Carlos Perez");
        clienteDto.setTelefono("911222333");

        Cliente resultado = clienteService.actualizarCliente(1, clienteDto);

        assertNotNull(resultado);
        assertEquals("Juan Carlos Perez", resultado.getNombre());
        assertEquals("911222333", resultado.getTelefono());
        verify(clienteRepository, times(1)).findById(1);
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException al actualizar cliente inexistente")
    void actualizarCliente_CuandoNoExiste_DebeLanzarExcepcion() {
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.actualizarCliente(99, clienteDto);
        });

        assertTrue(exception.getMessage().contains("Cliente no encontrado con ID: 99"));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar cliente por ID")
    void eliminarCliente_DebeInvocarDeleteById() {
        doNothing().when(clienteRepository).deleteById(1);

        clienteService.eliminarCliente(1);

        verify(clienteRepository, times(1)).deleteById(1);
    }
}
