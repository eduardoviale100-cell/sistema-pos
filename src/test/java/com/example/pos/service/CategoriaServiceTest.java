package com.example.pos.service;

import com.example.pos.dto.CategoriaRequestDto;
import com.example.pos.model.Categoria;
import com.example.pos.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequestDto dto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Dispositivos y accesorios electrónicos");

        dto = new CategoriaRequestDto();
        dto.setNombre("Electrónica");
        dto.setDescripcion("Dispositivos y accesorios electrónicos");
    }

    @Test
    @DisplayName("Debe listar todas las categorías")
    void listarTodos_DebeRetornarLista() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> lista = categoriaService.listarTodos();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Electrónica", lista.get(0).getNombre());
    }

    @Test
    @DisplayName("Debe guardar una nueva categoría")
    void guardarCategoria_Exitoso() {
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(i -> {
            Categoria c = i.getArgument(0);
            c.setId(1);
            return c;
        });

        Categoria guardada = categoriaService.guardarCategoria(dto);

        assertNotNull(guardada);
        assertEquals(1, guardada.getId());
        assertEquals("Electrónica", guardada.getNombre());
    }

    @Test
    @DisplayName("Debe actualizar categoría existente")
    void actualizarCategoria_Exitoso() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(i -> i.getArgument(0));

        dto.setNombre("Tecnología y Gadgets");

        Categoria actualizada = categoriaService.actualizarCategoria(1, dto);

        assertNotNull(actualizada);
        assertEquals("Tecnología y Gadgets", actualizada.getNombre());
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar categoría inexistente")
    void actualizarCategoria_NoExiste_LanzaExcepcion() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoriaService.actualizarCategoria(99, dto));
    }

    @Test
    @DisplayName("Debe eliminar categoría por ID")
    void eliminarCategoria_Exitoso() {
        doNothing().when(categoriaRepository).deleteById(1);

        assertDoesNotThrow(() -> categoriaService.eliminarCategoria(1));
        verify(categoriaRepository).deleteById(1);
    }
}
