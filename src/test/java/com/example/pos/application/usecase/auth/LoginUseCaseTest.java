package com.example.pos.application.usecase.auth;

import com.example.pos.domain.exception.CredencialesInvalidasException;
import com.example.pos.domain.exception.UsuarioInactivoException;
import com.example.pos.domain.model.usuario.RolUsuario;
import com.example.pos.domain.model.usuario.UsuarioDomain;
import com.example.pos.domain.repository.PasswordPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private PasswordPort passwordPort;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(usuarioRepositoryPort, passwordPort);
    }

    @Test
    @DisplayName("Debe autenticar correctamente con credenciales válidas y usuario activo")
    void login_Exitoso() {
        UsuarioDomain usuario = new UsuarioDomain(1L, "Admin User", "admin", "hash123", RolUsuario.ADMIN, true, null, null);

        when(usuarioRepositoryPort.buscarPorUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordPort.verificar("admin123", "hash123")).thenReturn(true);

        UsuarioDomain resultado = loginUseCase.ejecutar("admin", "admin123");

        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsername());
        assertEquals(RolUsuario.ADMIN, resultado.getRol());
        assertTrue(resultado.isActivo());
    }

    @Test
    @DisplayName("Debe lanzar CredencialesInvalidasException si el usuario no existe")
    void login_UsuarioNoExiste() {
        when(usuarioRepositoryPort.buscarPorUsername("inexistente")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class, () ->
                loginUseCase.ejecutar("inexistente", "pass123")
        );
    }

    @Test
    @DisplayName("Debe lanzar CredencialesInvalidasException si la contraseña no coincide")
    void login_PasswordIncorrecta() {
        UsuarioDomain usuario = new UsuarioDomain(1L, "Admin User", "admin", "hash123", RolUsuario.ADMIN, true, null, null);

        when(usuarioRepositoryPort.buscarPorUsername("admin")).thenReturn(Optional.of(usuario));
        when(passwordPort.verificar("wrongpass", "hash123")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () ->
                loginUseCase.ejecutar("admin", "wrongpass")
        );
    }

    @Test
    @DisplayName("Debe lanzar UsuarioInactivoException si el usuario está inactivo")
    void login_UsuarioInactivo() {
        UsuarioDomain usuario = new UsuarioDomain(1L, "Admin User", "admin", "hash123", RolUsuario.ADMIN, false, null, null);

        when(usuarioRepositoryPort.buscarPorUsername("admin")).thenReturn(Optional.of(usuario));

        assertThrows(UsuarioInactivoException.class, () ->
                loginUseCase.ejecutar("admin", "admin123")
        );
    }
}
