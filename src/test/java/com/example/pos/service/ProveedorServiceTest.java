package com.example.pos.service;

import com.example.pos.dto.ProveedorRequestDto;
import com.example.pos.model.Proveedor;
import com.example.pos.repository.ProveedorRepository;
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
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor;
    private ProveedorRequestDto dto;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1);
        proveedor.setNombre("Distribuidora Global");
        proveedor.setRucDni("20123456789");
        proveedor.setTelefono("999888777");
        proveedor.setEmail("contacto@distribuidora.com");

        dto = new ProveedorRequestDto();
        dto.setNombre("Distribuidora Global");
        dto.setRucDni("20123456789");
        dto.setTelefono("999888777");
        dto.setEmail("contacto@distribuidora.com");
    }

    @Test
    @DisplayName("Debe guardar un proveedor con su RUC/DNI proporcionado")
    void guardarProveedor_ConRuc_Exitoso() {
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(i -> {
            Proveedor p = i.getArgument(0);
            p.setId(1);
            return p;
        });

        Proveedor resultado = proveedorService.guardarProveedor(dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("20123456789", resultado.getRucDni());
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Debe generar un código PRV automático si el RUC/DNI viene vacío")
    void guardarProveedor_SinRuc_GeneraIdentificadorAuto() {
        dto.setRucDni(null);

        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(i -> i.getArgument(0));

        Proveedor resultado = proveedorService.guardarProveedor(dto);

        assertNotNull(resultado);
        assertTrue(resultado.getRucDni().startsWith("PRV-"));
    }

    @Test
    @DisplayName("Debe actualizar los datos de un proveedor existente")
    void actualizarProveedor_Exitoso() {
        when(proveedorRepository.findById(1)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(i -> i.getArgument(0));

        dto.setNombre("Distribuidora Global SAC");
        dto.setTelefono("955443322");

        Proveedor actualizado = proveedorService.actualizarProveedor(1, dto);

        assertNotNull(actualizado);
        assertEquals("Distribuidora Global SAC", actualizado.getNombre());
        assertEquals("955443322", actualizado.getTelefono());
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    @DisplayName("Debe listar todos los proveedores")
    void listarTodos_DebeRetornarLista() {
        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor));

        List<Proveedor> lista = proveedorService.listarTodos();

        assertNotNull(lista);
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Debe eliminar proveedor por ID")
    void eliminarProveedor_DebeInvocarDeleteById() {
        doNothing().when(proveedorRepository).deleteById(1);

        assertDoesNotThrow(() -> proveedorService.eliminarProveedor(1));
        verify(proveedorRepository).deleteById(1);
    }
}
