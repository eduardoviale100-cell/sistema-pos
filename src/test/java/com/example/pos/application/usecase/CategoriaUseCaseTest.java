package com.example.pos.application.usecase;

import com.example.pos.domain.exception.CategoriaDuplicadaException;
import com.example.pos.domain.exception.CategoriaNoEncontradaException;
import com.example.pos.domain.model.CategoriaDomain;
import com.example.pos.domain.repository.CategoriaRepositoryPort;
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
class CategoriaUseCaseTest {

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    private CrearCategoriaUseCase crearCategoriaUseCase;
    private ActualizarCategoriaUseCase actualizarCategoriaUseCase;
    private ListarCategoriasUseCase listarCategoriasUseCase;
    private BuscarCategoriaPorIdUseCase buscarCategoriaPorIdUseCase;
    private EliminarCategoriaUseCase eliminarCategoriaUseCase;

    @BeforeEach
    void setUp() {
        crearCategoriaUseCase = new CrearCategoriaUseCase(categoriaRepositoryPort);
        actualizarCategoriaUseCase = new ActualizarCategoriaUseCase(categoriaRepositoryPort);
        listarCategoriasUseCase = new ListarCategoriasUseCase(categoriaRepositoryPort);
        buscarCategoriaPorIdUseCase = new BuscarCategoriaPorIdUseCase(categoriaRepositoryPort);
        eliminarCategoriaUseCase = new EliminarCategoriaUseCase(categoriaRepositoryPort);
    }

    @Test
    @DisplayName("Debe crear una categoría si el nombre no existe")
    void crearCategoria_Exitoso() {
        CrearCategoriaCommand command = new CrearCategoriaCommand("Bebidas", "Gaseosas y aguas");

        when(categoriaRepositoryPort.existePorNombre("Bebidas")).thenReturn(false);
        when(categoriaRepositoryPort.guardar(any(CategoriaDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaDomain resultado = crearCategoriaUseCase.ejecutar(command);

        assertNotNull(resultado);
        assertEquals("Bebidas", resultado.getNombre());
        verify(categoriaRepositoryPort, times(1)).guardar(any(CategoriaDomain.class));
    }

    @Test
    @DisplayName("Debe lanzar CategoriaDuplicadaException al crear con nombre existente")
    void crearCategoria_NombreDuplicado_LanzaExcepcion() {
        CrearCategoriaCommand command = new CrearCategoriaCommand("Bebidas", "Desc");

        when(categoriaRepositoryPort.existePorNombre("Bebidas")).thenReturn(true);

        assertThrows(CategoriaDuplicadaException.class, () -> crearCategoriaUseCase.ejecutar(command));
        verify(categoriaRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe actualizar categoría existente")
    void actualizarCategoria_Exitoso() {
        CategoriaDomain categoria = new CategoriaDomain(1L, "Original", "Desc");
        ActualizarCategoriaCommand command = new ActualizarCategoriaCommand("Actualizado", "Nueva desc");

        when(categoriaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepositoryPort.buscarPorNombre("Actualizado")).thenReturn(Optional.empty());
        when(categoriaRepositoryPort.guardar(any(CategoriaDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaDomain resultado = actualizarCategoriaUseCase.ejecutar(1L, command);

        assertEquals("Actualizado", resultado.getNombre());
        assertEquals("Nueva desc", resultado.getDescripcion());
    }

    @Test
    @DisplayName("Debe eliminar categoría si existe")
    void eliminarCategoria_Exitoso() {
        when(categoriaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(new CategoriaDomain(1L, "Cat", "")));

        eliminarCategoriaUseCase.ejecutar(1L);

        verify(categoriaRepositoryPort, times(1)).eliminarPorId(1L);
    }

    @Test
    @DisplayName("Debe lanzar CategoriaNoEncontradaException al eliminar inexistente")
    void eliminarCategoria_NoExiste_LanzaExcepcion() {
        when(categoriaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNoEncontradaException.class, () -> eliminarCategoriaUseCase.ejecutar(99L));
        verify(categoriaRepositoryPort, never()).eliminarPorId(any());
    }
}
