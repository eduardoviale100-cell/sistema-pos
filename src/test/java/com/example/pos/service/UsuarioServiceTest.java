package com.example.pos.service;

import com.example.pos.dto.UsuarioRequestDto;
import com.example.pos.model.Usuario;
import com.example.pos.repository.UsuarioRepository;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRequestDto dto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Eduardo Viale");
        usuario.setUsuario("eviale");
        usuario.setPassword("123456");
        usuario.setRol("ADMIN");
        usuario.setActivo(true);

        dto = new UsuarioRequestDto();
        dto.setNombre("Eduardo Viale");
        dto.setUsuario("eviale");
        dto.setPassword("123456");
        dto.setRol("ADMIN");
        dto.setActivo(true);
    }

    @Test
    @DisplayName("Debe registrar un nuevo usuario cuando el username está disponible")
    void guardarUsuario_Exitoso() {
        when(usuarioRepository.existsByUsuario("eviale")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            u.setId(1);
            return u;
        });

        Usuario resultado = usuarioService.guardarUsuario(dto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("eviale", resultado.getUsuario());
        assertEquals("ADMIN", resultado.getRol());
        assertTrue(resultado.getActivo());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el nombre de usuario ya existe")
    void guardarUsuario_UsuarioYaExiste_LanzaExcepcion() {
        when(usuarioRepository.existsByUsuario("eviale")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.guardarUsuario(dto));
        assertTrue(ex.getMessage().contains("ya está registrado"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cambiar el estado activo/inactivo de un usuario")
    void cambiarEstado_DebeActualizarActivo() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario inactivo = usuarioService.cambiarEstado(1, false);

        assertNotNull(inactivo);
        assertFalse(inactivo.getActivo());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe actualizar un usuario correctamente si el nombre de usuario no entra en conflicto")
    void actualizarUsuario_Exitoso() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        dto.setNombre("Eduardo Actualizado");
        dto.setRol("SUPERVISOR");

        Usuario actualizado = usuarioService.actualizarUsuario(1, dto);

        assertNotNull(actualizado);
        assertEquals("Eduardo Actualizado", actualizado.getNombre());
        assertEquals("SUPERVISOR", actualizado.getRol());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException al actualizar si el nuevo username ya lo usa otro usuario")
    void actualizarUsuario_UsernameDuplicado_LanzaExcepcion() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByUsuario("otropersonaje")).thenReturn(true);

        dto.setUsuario("otropersonaje");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.actualizarUsuario(1, dto));
        assertTrue(ex.getMessage().contains("ya está ocupado"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void listarTodos_DebeRetornarLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}
