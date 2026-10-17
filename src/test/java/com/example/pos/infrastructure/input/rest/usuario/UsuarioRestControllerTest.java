package com.example.pos.infrastructure.input.rest.usuario;

import com.example.pos.application.usecase.usuario.*;
import com.example.pos.domain.model.usuario.RolUsuario;
import com.example.pos.domain.model.usuario.UsuarioDomain;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRestControllerTest {

    @Mock
    private CrearUsuarioUseCase crearUsuarioUseCase;

    @Mock
    private ActualizarUsuarioUseCase actualizarUsuarioUseCase;

    @Mock
    private CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;

    @Mock
    private ListarUsuariosUseCase listarUsuariosUseCase;

    @Mock
    private BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;

    @Mock
    private EliminarUsuarioUseCase eliminarUsuarioUseCase;

    @InjectMocks
    private UsuarioRestController usuarioRestController;

    private UsuarioDomain usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioDomain(1L, "Admin User", "admin", "hash123", RolUsuario.ADMIN, true, null, null);
    }

    @Test
    @DisplayName("GET /api/usuarios debe retornar 200 OK y lista con UsuarioResponseDto")
    void listar_DebeRetornar200YListaDto() {
        when(listarUsuariosUseCase.ejecutar(false, null)).thenReturn(List.of(usuario));

        ResponseEntity<List<UsuarioResponseDto>> response = usuarioRestController.listar(false, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        UsuarioResponseDto dto = response.getBody().get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Admin User", dto.getNombre());
        assertEquals("admin", dto.getUsername());
        assertEquals("admin", dto.getUsuario());
        assertEquals("ADMIN", dto.getRol());
        assertTrue(dto.getActivo());

        verify(listarUsuariosUseCase).ejecutar(false, null);
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} debe retornar 200 OK y UsuarioResponseDto")
    void buscarPorId_DebeRetornar200YUsuarioDto() {
        when(buscarUsuarioPorIdUseCase.ejecutar(1L)).thenReturn(usuario);

        ResponseEntity<UsuarioResponseDto> response = usuarioRestController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("admin", response.getBody().getUsername());
        verify(buscarUsuarioPorIdUseCase).ejecutar(1L);
    }

    @Test
    @DisplayName("POST /api/usuarios debe retornar 201 CREATED y UsuarioResponseDto")
    void crear_DebeRetornar201YUsuarioDto() {
        CrearUsuarioRequestDto dto = CrearUsuarioRequestDto.builder()
                .nombre("Nuevo Usuario")
                .username("nuevou")
                .password("pass123")
                .rol("VENDEDOR")
                .build();

        UsuarioDomain nuevo = new UsuarioDomain(2L, "Nuevo Usuario", "nuevou", "hash456", RolUsuario.VENDEDOR, true, null, null);
        when(crearUsuarioUseCase.ejecutar(any(CrearUsuarioCommand.class))).thenReturn(nuevo);

        ResponseEntity<UsuarioResponseDto> response = usuarioRestController.crear(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals("nuevou", response.getBody().getUsername());
        verify(crearUsuarioUseCase).ejecutar(any(CrearUsuarioCommand.class));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} debe retornar 200 OK y UsuarioResponseDto actualizado")
    void actualizar_DebeRetornar200YUsuarioDto() {
        ActualizarUsuarioRequestDto dto = ActualizarUsuarioRequestDto.builder()
                .nombre("Admin Actualizado")
                .username("admin")
                .password(null)
                .rol("ADMIN")
                .build();

        UsuarioDomain actualizado = new UsuarioDomain(1L, "Admin Actualizado", "admin", "hash123", RolUsuario.ADMIN, true, null, null);
        when(actualizarUsuarioUseCase.ejecutar(eq(1L), any(ActualizarUsuarioCommand.class))).thenReturn(actualizado);

        ResponseEntity<UsuarioResponseDto> response = usuarioRestController.actualizar(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Admin Actualizado", response.getBody().getNombre());
        verify(actualizarUsuarioUseCase).ejecutar(eq(1L), any(ActualizarUsuarioCommand.class));
    }

    @Test
    @DisplayName("PATCH /api/usuarios/{id}/estado debe retornar 200 OK y UsuarioResponseDto con nuevo estado")
    void cambiarEstado_DebeRetornar200YUsuarioDto() {
        UsuarioDomain inactivo = new UsuarioDomain(1L, "Admin User", "admin", "hash123", RolUsuario.ADMIN, false, null, null);
        when(cambiarEstadoUsuarioUseCase.ejecutar(1L, false)).thenReturn(inactivo);

        ResponseEntity<UsuarioResponseDto> response = usuarioRestController.cambiarEstado(1L, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getActivo());
        verify(cambiarEstadoUsuarioUseCase).ejecutar(1L, false);
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} debe retornar 204 NO CONTENT")
    void eliminar_DebeRetornar204NoContent() {
        doNothing().when(eliminarUsuarioUseCase).ejecutar(1L);

        ResponseEntity<Void> response = usuarioRestController.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eliminarUsuarioUseCase).ejecutar(1L);
    }
}
