package com.example.pos.application.usecase;

import com.example.pos.domain.exception.UsuarioDuplicadoException;
import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import com.example.pos.domain.model.RolUsuario;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.PasswordPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Actualizar los datos de un usuario existente.
 *
 * <p>La contraseña se actualiza solo si se proporciona un valor no vacío.</p>
 */
@Service
public class ActualizarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordPort passwordPort;

    public ActualizarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
                                     PasswordPort passwordPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordPort = passwordPort;
    }

    /**
     * Actualiza el usuario identificado por {@code id}.
     *
     * @param id      Identificador único del usuario.
     * @param command Datos actualizados.
     * @return {@link UsuarioDomain} actualizado.
     * @throws UsuarioNoEncontradoException si no existe el usuario.
     * @throws UsuarioDuplicadoException    si el nuevo username ya está en uso por otro usuario.
     * @throws IllegalArgumentException     si el ID o el command son nulos.
     */
    @Transactional
    public UsuarioDomain ejecutar(Long id, ActualizarUsuarioCommand command) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }
        if (command == null) {
            throw new IllegalArgumentException("El comando de actualización no puede ser nulo.");
        }

        UsuarioDomain usuario = usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> UsuarioNoEncontradoException.porId(id));

        // Validar unicidad de username si cambió
        if (!usuario.getUsername().equalsIgnoreCase(command.username())) {
            if (usuarioRepositoryPort.existePorUsernameExcluyendoId(command.username(), id)) {
                throw UsuarioDuplicadoException.porUsername(command.username());
            }
        }

        // Encriptar nueva contraseña solo si se proporcionó
        String nuevoHash = null;
        if (command.passwordPlano() != null && !command.passwordPlano().isBlank()) {
            nuevoHash = passwordPort.encriptar(command.passwordPlano());
        }

        RolUsuario nuevoRol = command.rol() != null ? RolUsuario.fromString(command.rol()) : usuario.getRol();

        usuario.actualizarPerfil(command.nombre(), command.username(), nuevoHash, nuevoRol);

        return usuarioRepositoryPort.guardar(usuario);
    }
}
