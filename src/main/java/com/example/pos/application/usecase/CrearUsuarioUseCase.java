package com.example.pos.application.usecase;

import com.example.pos.domain.exception.UsuarioDuplicadoException;
import com.example.pos.domain.model.RolUsuario;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.PasswordPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Registrar un nuevo usuario en el sistema.
 *
 * <p>Valida unicidad de username, encripta la contraseña y persiste el usuario.</p>
 */
@Service
public class CrearUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordPort passwordPort;

    public CrearUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                PasswordPort passwordPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordPort = passwordPort;
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param command Datos del nuevo usuario.
     * @return {@link UsuarioDomain} persistido.
     * @throws UsuarioDuplicadoException si el username ya está en uso.
     * @throws IllegalArgumentException  si el comando es nulo o el rol es inválido.
     */
    @Transactional
    public UsuarioDomain ejecutar(CrearUsuarioCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de creación de usuario no puede ser nulo.");
        }

        if (usuarioRepositoryPort.existePorUsername(command.username())) {
            throw UsuarioDuplicadoException.porUsername(command.username());
        }

        RolUsuario rol = RolUsuario.fromString(command.rol());
        String hash = passwordPort.encriptar(command.passwordPlano());

        UsuarioDomain nuevoUsuario = new UsuarioDomain(
                command.nombre(),
                command.username(),
                hash,
                rol
        );

        return usuarioRepositoryPort.guardar(nuevoUsuario);
    }
}
