package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MarcaDuplicadaException;
import com.example.pos.domain.exception.MarcaNoEncontradaException;
import com.example.pos.domain.model.MarcaDomain;
import com.example.pos.domain.repository.MarcaRepositoryPort;
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
class MarcaUseCaseTest {

    @Mock
    private MarcaRepositoryPort marcaRepositoryPort;

    private CrearMarcaUseCase crearMarcaUseCase;
    private ActualizarMarcaUseCase actualizarMarcaUseCase;
    private ListarMarcasUseCase listarMarcasUseCase;
    private BuscarMarcaPorIdUseCase buscarMarcaPorIdUseCase;
    private EliminarMarcaUseCase eliminarMarcaUseCase;

    @BeforeEach
    void setUp() {
        crearMarcaUseCase = new CrearMarcaUseCase(marcaRepositoryPort);
        actualizarMarcaUseCase = new ActualizarMarcaUseCase(marcaRepositoryPort);
        listarMarcasUseCase = new ListarMarcasUseCase(marcaRepositoryPort);
        buscarMarcaPorIdUseCase = new BuscarMarcaPorIdUseCase(marcaRepositoryPort);
        eliminarMarcaUseCase = new EliminarMarcaUseCase(marcaRepositoryPort);
    }

    @Test
    @DisplayName("Debe crear una marca si el nombre no existe")
    void crearMarca_Exitoso() {
        CrearMarcaCommand command = new CrearMarcaCommand("Sony", "Electronica");

        when(marcaRepositoryPort.existePorNombre("Sony")).thenReturn(false);
        when(marcaRepositoryPort.guardar(any(MarcaDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MarcaDomain resultado = crearMarcaUseCase.ejecutar(command);

        assertNotNull(resultado);
        assertEquals("Sony", resultado.getNombre());
        verify(marcaRepositoryPort, times(1)).guardar(any(MarcaDomain.class));
    }

    @Test
    @DisplayName("Debe lanzar MarcaDuplicadaException al crear con nombre existente")
    void crearMarca_NombreDuplicado_LanzaExcepcion() {
        CrearMarcaCommand command = new CrearMarcaCommand("Sony", "Desc");

        when(marcaRepositoryPort.existePorNombre("Sony")).thenReturn(true);

        assertThrows(MarcaDuplicadaException.class, () -> crearMarcaUseCase.ejecutar(command));
        verify(marcaRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe actualizar marca existente")
    void actualizarMarca_Exitoso() {
        MarcaDomain marca = new MarcaDomain(1L, "Original", "Desc");
        ActualizarMarcaCommand command = new ActualizarMarcaCommand("Actualizado", "Nueva desc");

        when(marcaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(marca));
        when(marcaRepositoryPort.buscarPorNombre("Actualizado")).thenReturn(Optional.empty());
        when(marcaRepositoryPort.guardar(any(MarcaDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MarcaDomain resultado = actualizarMarcaUseCase.ejecutar(1L, command);

        assertEquals("Actualizado", resultado.getNombre());
        assertEquals("Nueva desc", resultado.getDescripcion());
    }

    @Test
    @DisplayName("Debe eliminar marca si existe")
    void eliminarMarca_Exitoso() {
        when(marcaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(new MarcaDomain(1L, "Marca1", "")));

        eliminarMarcaUseCase.ejecutar(1L);

        verify(marcaRepositoryPort, times(1)).eliminarPorId(1L);
    }

    @Test
    @DisplayName("Debe lanzar MarcaNoEncontradaException al eliminar inexistente")
    void eliminarMarca_NoExiste_LanzaExcepcion() {
        when(marcaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(MarcaNoEncontradaException.class, () -> eliminarMarcaUseCase.ejecutar(99L));
        verify(marcaRepositoryPort, never()).eliminarPorId(any());
    }
}
