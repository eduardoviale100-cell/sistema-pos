package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MetodoPagoDuplicadoException;
import com.example.pos.domain.exception.MetodoPagoNoEncontradoException;
import com.example.pos.domain.model.MetodoPagoDomain;
import com.example.pos.domain.repository.MetodoPagoRepositoryPort;
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
class MetodoPagoUseCaseTest {

    @Mock
    private MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    private CrearMetodoPagoUseCase crearMetodoPagoUseCase;
    private ActualizarMetodoPagoUseCase actualizarMetodoPagoUseCase;
    private CambiarEstadoMetodoPagoUseCase cambiarEstadoMetodoPagoUseCase;
    private ListarMetodosPagoUseCase listarMetodosPagoUseCase;
    private BuscarMetodoPagoPorIdUseCase buscarMetodoPagoPorIdUseCase;
    private EliminarMetodoPagoUseCase eliminarMetodoPagoUseCase;

    @BeforeEach
    void setUp() {
        crearMetodoPagoUseCase = new CrearMetodoPagoUseCase(metodoPagoRepositoryPort);
        actualizarMetodoPagoUseCase = new ActualizarMetodoPagoUseCase(metodoPagoRepositoryPort);
        cambiarEstadoMetodoPagoUseCase = new CambiarEstadoMetodoPagoUseCase(metodoPagoRepositoryPort);
        listarMetodosPagoUseCase = new ListarMetodosPagoUseCase(metodoPagoRepositoryPort);
        buscarMetodoPagoPorIdUseCase = new BuscarMetodoPagoPorIdUseCase(metodoPagoRepositoryPort);
        eliminarMetodoPagoUseCase = new EliminarMetodoPagoUseCase(metodoPagoRepositoryPort);
    }

    // ─── CrearMetodoPagoUseCase ────────────────────────────────────────────────

    @Test
    @DisplayName("Debe crear un método de pago si el nombre no existe")
    void crearMetodoPago_Exitoso() {
        CrearMetodoPagoCommand command = new CrearMetodoPagoCommand("Efectivo", true);

        when(metodoPagoRepositoryPort.existePorNombre("Efectivo")).thenReturn(false);
        when(metodoPagoRepositoryPort.guardar(any(MetodoPagoDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MetodoPagoDomain resultado = crearMetodoPagoUseCase.ejecutar(command);

        assertNotNull(resultado);
        assertEquals("Efectivo", resultado.getNombre());
        assertTrue(resultado.isActivo());
        verify(metodoPagoRepositoryPort, times(1)).guardar(any(MetodoPagoDomain.class));
    }

    @Test
    @DisplayName("Debe lanzar MetodoPagoDuplicadoException al crear con nombre existente")
    void crearMetodoPago_NombreDuplicado_LanzaExcepcion() {
        CrearMetodoPagoCommand command = new CrearMetodoPagoCommand("Tarjeta", true);

        when(metodoPagoRepositoryPort.existePorNombre("Tarjeta")).thenReturn(true);

        assertThrows(MetodoPagoDuplicadoException.class,
                () -> crearMetodoPagoUseCase.ejecutar(command));
        verify(metodoPagoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el command es nulo")
    void crearMetodoPago_CommandNulo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> crearMetodoPagoUseCase.ejecutar(null));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el nombre está en blanco")
    void crearMetodoPago_NombreVacio_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new MetodoPagoDomain("  ", true));
    }

    // ─── ActualizarMetodoPagoUseCase ──────────────────────────────────────────

    @Test
    @DisplayName("Debe actualizar un método de pago existente")
    void actualizarMetodoPago_Exitoso() {
        MetodoPagoDomain existente = new MetodoPagoDomain(1L, "Efectivo", true);
        ActualizarMetodoPagoCommand command = new ActualizarMetodoPagoCommand("Efectivo Actualizado", true);

        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(metodoPagoRepositoryPort.buscarPorNombre("Efectivo Actualizado")).thenReturn(Optional.empty());
        when(metodoPagoRepositoryPort.guardar(any(MetodoPagoDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MetodoPagoDomain resultado = actualizarMetodoPagoUseCase.ejecutar(1L, command);

        assertEquals("Efectivo Actualizado", resultado.getNombre());
    }

    @Test
    @DisplayName("Debe lanzar MetodoPagoNoEncontradoException al actualizar ID inexistente")
    void actualizarMetodoPago_NoExiste_LanzaExcepcion() {
        ActualizarMetodoPagoCommand command = new ActualizarMetodoPagoCommand("Yape", true);

        when(metodoPagoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(MetodoPagoNoEncontradoException.class,
                () -> actualizarMetodoPagoUseCase.ejecutar(99L, command));
    }

    @Test
    @DisplayName("Debe lanzar MetodoPagoDuplicadoException al actualizar con nombre de otro registro")
    void actualizarMetodoPago_NombreDuplicadoDeOtro_LanzaExcepcion() {
        MetodoPagoDomain existente = new MetodoPagoDomain(1L, "Efectivo", true);
        MetodoPagoDomain otro = new MetodoPagoDomain(2L, "Tarjeta", true);
        ActualizarMetodoPagoCommand command = new ActualizarMetodoPagoCommand("Tarjeta", true);

        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(metodoPagoRepositoryPort.buscarPorNombre("Tarjeta")).thenReturn(Optional.of(otro));

        assertThrows(MetodoPagoDuplicadoException.class,
                () -> actualizarMetodoPagoUseCase.ejecutar(1L, command));
    }

    // ─── CambiarEstadoMetodoPagoUseCase ───────────────────────────────────────

    @Test
    @DisplayName("Debe desactivar un método de pago activo")
    void cambiarEstado_Desactivar_Exitoso() {
        MetodoPagoDomain activo = new MetodoPagoDomain(1L, "Efectivo", true);

        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(activo));
        when(metodoPagoRepositoryPort.guardar(any(MetodoPagoDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MetodoPagoDomain resultado = cambiarEstadoMetodoPagoUseCase.ejecutar(1L, false);

        assertFalse(resultado.isActivo());
        verify(metodoPagoRepositoryPort, times(1)).guardar(any());
    }

    @Test
    @DisplayName("Debe activar un método de pago inactivo")
    void cambiarEstado_Activar_Exitoso() {
        MetodoPagoDomain inactivo = new MetodoPagoDomain(1L, "Cheque", false);

        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(inactivo));
        when(metodoPagoRepositoryPort.guardar(any(MetodoPagoDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MetodoPagoDomain resultado = cambiarEstadoMetodoPagoUseCase.ejecutar(1L, true);

        assertTrue(resultado.isActivo());
    }

    @Test
    @DisplayName("Debe lanzar MetodoPagoNoEncontradoException al cambiar estado de ID inexistente")
    void cambiarEstado_NoExiste_LanzaExcepcion() {
        when(metodoPagoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(MetodoPagoNoEncontradoException.class,
                () -> cambiarEstadoMetodoPagoUseCase.ejecutar(99L, false));
    }

    // ─── ListarMetodosPagoUseCase ──────────────────────────────────────────────

    @Test
    @DisplayName("Debe listar todos los métodos de pago")
    void listarTodos_Exitoso() {
        List<MetodoPagoDomain> lista = List.of(
                new MetodoPagoDomain(1L, "Efectivo", true),
                new MetodoPagoDomain(2L, "Tarjeta", false)
        );

        when(metodoPagoRepositoryPort.listarTodos()).thenReturn(lista);

        List<MetodoPagoDomain> resultado = listarMetodosPagoUseCase.ejecutar(false);

        assertEquals(2, resultado.size());
        verify(metodoPagoRepositoryPort, times(1)).listarTodos();
        verify(metodoPagoRepositoryPort, never()).listarActivos();
    }

    @Test
    @DisplayName("Debe listar solo los métodos de pago activos")
    void listarSoloActivos_Exitoso() {
        List<MetodoPagoDomain> activos = List.of(
                new MetodoPagoDomain(1L, "Efectivo", true)
        );

        when(metodoPagoRepositoryPort.listarActivos()).thenReturn(activos);

        List<MetodoPagoDomain> resultado = listarMetodosPagoUseCase.ejecutar(true);

        assertEquals(1, resultado.size());
        verify(metodoPagoRepositoryPort, times(1)).listarActivos();
        verify(metodoPagoRepositoryPort, never()).listarTodos();
    }

    // ─── BuscarMetodoPagoPorIdUseCase ────────────────────────────────────────

    @Test
    @DisplayName("Debe encontrar un método de pago por ID")
    void buscarPorId_Exitoso() {
        MetodoPagoDomain metodo = new MetodoPagoDomain(1L, "Yape", true);

        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(metodo));

        MetodoPagoDomain resultado = buscarMetodoPagoPorIdUseCase.ejecutar(1L);

        assertNotNull(resultado);
        assertEquals("Yape", resultado.getNombre());
    }

    @Test
    @DisplayName("Debe lanzar MetodoPagoNoEncontradoException al buscar ID inexistente")
    void buscarPorId_NoExiste_LanzaExcepcion() {
        when(metodoPagoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(MetodoPagoNoEncontradoException.class,
                () -> buscarMetodoPagoPorIdUseCase.ejecutar(99L));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException al buscar con ID nulo")
    void buscarPorId_IdNulo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> buscarMetodoPagoPorIdUseCase.ejecutar(null));
    }

    // ─── EliminarMetodoPagoUseCase ────────────────────────────────────────────

    @Test
    @DisplayName("Debe eliminar un método de pago si existe")
    void eliminarMetodoPago_Exitoso() {
        MetodoPagoDomain metodo = new MetodoPagoDomain(1L, "Efectivo", true);

        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(metodo));

        eliminarMetodoPagoUseCase.ejecutar(1L);

        verify(metodoPagoRepositoryPort, times(1)).eliminarPorId(1L);
    }

    @Test
    @DisplayName("Debe lanzar MetodoPagoNoEncontradoException al eliminar ID inexistente")
    void eliminarMetodoPago_NoExiste_LanzaExcepcion() {
        when(metodoPagoRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(MetodoPagoNoEncontradoException.class,
                () -> eliminarMetodoPagoUseCase.ejecutar(99L));
        verify(metodoPagoRepositoryPort, never()).eliminarPorId(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException al eliminar con ID nulo")
    void eliminarMetodoPago_IdNulo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> eliminarMetodoPagoUseCase.ejecutar(null));
    }
}
