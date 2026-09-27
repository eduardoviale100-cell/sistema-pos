package com.example.pos.service;

import com.example.pos.dto.MetodoPagoRequestDto;
import com.example.pos.model.MetodoPago;
import com.example.pos.repository.MetodoPagoRepository;
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
class MetodoPagoServiceTest {

    @Mock
    private MetodoPagoRepository metodoPagoRepository;

    @InjectMocks
    private MetodoPagoService metodoPagoService;

    private MetodoPago metodo;
    private MetodoPagoRequestDto dto;

    @BeforeEach
    void setUp() {
        metodo = new MetodoPago();
        metodo.setId(1);
        metodo.setNombre("Yape");
        metodo.setActivo(true);

        dto = new MetodoPagoRequestDto();
        dto.setNombre("Yape");
        dto.setActivo(true);
    }

    @Test
    @DisplayName("Debe listar métodos de pago activos")
    void listarActivos_DebeRetornarSoloActivos() {
        when(metodoPagoRepository.findByActivoTrue()).thenReturn(List.of(metodo));

        List<MetodoPago> activos = metodoPagoService.listarActivos();

        assertNotNull(activos);
        assertEquals(1, activos.size());
        assertTrue(activos.get(0).getActivo());
    }

    @Test
    @DisplayName("Debe cambiar el estado del método de pago")
    void cambiarEstado_DebeModificarActivo() {
        when(metodoPagoRepository.findById(1)).thenReturn(Optional.of(metodo));
        when(metodoPagoRepository.save(any(MetodoPago.class))).thenAnswer(i -> i.getArgument(0));

        MetodoPago resultado = metodoPagoService.cambiarEstado(1, false);

        assertNotNull(resultado);
        assertFalse(resultado.getActivo());
        verify(metodoPagoRepository).save(metodo);
    }

    @Test
    @DisplayName("Debe guardar un nuevo método de pago")
    void guardarMetodoPago_Exitoso() {
        when(metodoPagoRepository.save(any(MetodoPago.class))).thenAnswer(i -> {
            MetodoPago m = i.getArgument(0);
            m.setId(10);
            return m;
        });

        MetodoPago guardado = metodoPagoService.guardarMetodoPago(dto);

        assertNotNull(guardado);
        assertEquals(10, guardado.getId());
        assertEquals("Yape", guardado.getNombre());
    }
}
