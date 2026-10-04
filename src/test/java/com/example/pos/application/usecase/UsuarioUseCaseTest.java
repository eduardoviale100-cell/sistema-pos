package com.example.pos.application.usecase;

import com.example.pos.domain.exception.UsuarioDuplicadoException;
import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import com.example.pos.domain.model.RolUsuario;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.PasswordPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
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
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordPort passwordPort;

    private CrearUsuarioUseCase crearUsuarioUseCase;
    private ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
    private ListarUsuariosUseCase listarUsuariosUseCase;
    private BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private EliminarUsuarioUseCase eliminarUsuarioUseCase;

    @BeforeEach
    void setUp() {
        crearUsuarioUseCase = new CrearUsuarioUseCase(usuarioRepositoryPort, passwordPort);
        actualizarUsuarioUseCase = new ActualizarUsuarioUseCase(usuarioRepositoryPort, passwordPort);
        cambiarEstadoUsuarioUseCase = new CambiarEstadoUsuarioUseCase(usuarioRepositoryPort);
        listarUsuariosUseCase = new ListarUsuariosUseCase(usuarioRepositoryPort);
        buscarUsuarioPorIdUseCase = new BuscarUsuarioPorIdUseCase(usuarioRepositoryPort);
        eliminarUsuarioUseCase = new EliminarUsuarioUseCase(usuarioRepositoryPort);
    }

    // ─── CrearUsuarioUseCase ────────────────────────────────────────────────

    @Test
    @DisplayName("Debe crear un usuario si el username no existe")
    void crearUsuario_Exitoso() {
        CrearUsuarioCommand command = new CrearUsuarioCommand("Juan Perez", "juanp", "123456", "CAJERO");

        when(usuarioRepositoryPort.existePorUsername("juanp")).thenReturn(false);
        when(passwordPort.encriptar("123456")).thenReturn("hash123");
        when(usuarioRepositoryPort.guardar(any(UsuarioDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDomain resultado = crearUsuarioUseCase.ejecutar(command);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals("juanp", resultado.getUsername());
        assertEquals("hash123", resultado.getPasswordHash());
        assertEquals(RolUsuario.CAJERO, resultado.getRol());
        assertTrue(resultado.isActivo());
        verify(usuarioRepositoryPort, times(1)).guardar(any(UsuarioDomain.class));
    }

    @Test
    @DisplayName("Debe lanzar UsuarioDuplicadoException al crear con username existente")
    void crearUsuario_UsernameDuplicado_LanzaExcepcion() {
        CrearUsuarioCommand command = new CrearUsuarioCommand("Juan Perez", "juanp", "123456", "VENDEDOR");

        when(usuarioRepositoryPort.existePorUsername("juanp")).thenReturn(true);

        assertThrows(UsuarioDuplicadoException.class,
                () -> crearUsuarioUseCase.ejecutar(command));
        verify(usuarioRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si username es invalido")
    void crearUsuario_UsernameInvalido_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new UsuarioDomain("Juan Perez", "juan@p", "hash", RolUsuario.VENDEDOR));
    }

    // ─── ActualizarUsuarioUseCase ──────────────────────────────────────────

    @Test
    @DisplayName("Debe actualizar un usuario cambiando contraseña si se provee")
    void actualizarUsuario_ConPassword_Exitoso() {
        UsuarioDomain existente = new UsuarioDomain(1L, "Juan", "juanp", "hashviejo", RolUsuario.VENDEDOR, true, null, null);
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand("Juan Actualizado", "juanp", "nuevaPass", "ADMIN");

        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(passwordPort.encriptar("nuevaPass")).thenReturn("hashnuevo");
        when(usuarioRepositoryPort.guardar(any(UsuarioDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDomain resultado = actualizarUsuarioUseCase.ejecutar(1L, command);

        assertEquals("Juan Actualizado", resultado.getNombre());
        assertEquals("hashnuevo", resultado.getPasswordHash());
        assertEquals(RolUsuario.ADMIN, resultado.getRol());
    }

    @Test
    @DisplayName("Debe actualizar sin cambiar contraseña si no se provee")
    void actualizarUsuario_SinPassword_Exitoso() {
        UsuarioDomain existente = new UsuarioDomain(1L, "Juan", "juanp", "hashviejo", RolUsuario.VENDEDOR, true, null, null);
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand("Juan", "juanp", null, "VENDEDOR");

        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepositoryPort.guardar(any(UsuarioDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDomain resultado = actualizarUsuarioUseCase.ejecutar(1L, command);

        assertEquals("hashviejo", resultado.getPasswordHash()); // No debe cambiar
        verify(passwordPort, never()).encriptar(anyString());
    }

    @Test
    @DisplayName("Debe lanzar UsuarioDuplicadoException al actualizar con username de otro")
    void actualizarUsuario_UsernameDuplicadoDeOtro_LanzaExcepcion() {
        UsuarioDomain existente = new UsuarioDomain(1L, "Juan", "juanp", "hash", RolUsuario.VENDEDOR, true, null, null);
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand("Juan", "pedrop", null, "VENDEDOR");

        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepositoryPort.existePorUsernameExcluyendoId("pedrop", 1L)).thenReturn(true);

        assertThrows(UsuarioDuplicadoException.class,
                () -> actualizarUsuarioUseCase.ejecutar(1L, command));
    }

    // ─── CambiarEstadoUsuarioUseCase ───────────────────────────────────────

    @Test
    @DisplayName("Debe desactivar un usuario activo")
    void cambiarEstado_Desactivar_Exitoso() {
        UsuarioDomain activo = new UsuarioDomain(1L, "Juan", "juanp", "hash", RolUsuario.VENDEDOR, true, null, null);

        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(activo));
        when(usuarioRepositoryPort.guardar(any(UsuarioDomain.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDomain resultado = cambiarEstadoUsuarioUseCase.ejecutar(1L, false);

        assertFalse(resultado.isActivo());
    }

    // ─── ListarUsuariosUseCase ──────────────────────────────────────────────

    @Test
    @DisplayName("Debe listar todos por defecto")
    void listarTodos_Exitoso() {
        List<UsuarioDomain> lista = List.of(
                new UsuarioDomain(1L, "Juan", "juan", "h", RolUsuario.VENDEDOR, true, null, null),
                new UsuarioDomain(2L, "Pedro", "pedro", "h", RolUsuario.ADMIN, false, null, null)
        );

        when(usuarioRepositoryPort.listarTodos()).thenReturn(lista);

        List<UsuarioDomain> resultado = listarUsuariosUseCase.ejecutar(false, null);

        assertEquals(2, resultado.size());
        verify(usuarioRepositoryPort, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Debe listar solo activos si se especifica")
    void listarActivos_Exitoso() {
        when(usuarioRepositoryPort.listarActivos()).thenReturn(List.of());

        listarUsuariosUseCase.ejecutar(true, null);

        verify(usuarioRepositoryPort, times(1)).listarActivos();
    }

    @Test
    @DisplayName("Debe listar por rol si se especifica")
    void listarPorRol_Exitoso() {
        when(usuarioRepositoryPort.listarPorRol(RolUsuario.ADMIN)).thenReturn(List.of());

        listarUsuariosUseCase.ejecutar(false, "ADMIN");

        verify(usuarioRepositoryPort, times(1)).listarPorRol(RolUsuario.ADMIN);
    }

    // ─── EliminarUsuarioUseCase ────────────────────────────────────────────

    @Test
    @DisplayName("Debe eliminar un usuario si existe")
    void eliminarUsuario_Exitoso() {
        UsuarioDomain usuario = new UsuarioDomain(1L, "Juan", "juanp", "h", RolUsuario.VENDEDOR, true, null, null);

        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        eliminarUsuarioUseCase.ejecutar(1L);

        verify(usuarioRepositoryPort, times(1)).eliminarPorId(1L);
    }
}
