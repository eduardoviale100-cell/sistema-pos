package com.example.pos.application.usecase.auth;

import com.example.pos.domain.exception.CredencialesInvalidasException;
import com.example.pos.domain.exception.UsuarioInactivoException;
import com.example.pos.domain.model.usuario.UsuarioDomain;
import com.example.pos.domain.repository.PasswordPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Autenticación de usuarios en el sistema POS.
 *
 * <p>Valida credenciales contra la base de datos y comprueba que el usuario
 * esté en estado activo.</p>
 */
@Service
public class LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordPort passwordPort;

    public LoginUseCase(UsuarioRepositoryPort usuarioRepositoryPort, PasswordPort passwordPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordPort = passwordPort;
    }

    @Transactional(readOnly = true)
    public UsuarioDomain ejecutar(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new CredencialesInvalidasException();
        }

        UsuarioDomain usuario = usuarioRepositoryPort.buscarPorUsername(username.trim())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!usuario.isActivo()) {
            throw new UsuarioInactivoException(usuario.getUsername());
        }

        boolean passwordValida = passwordPort.verificar(rawPassword, usuario.getPasswordHash());
        if (!passwordValida) {
            throw new CredencialesInvalidasException();
        }

        return usuario;
    }
}
