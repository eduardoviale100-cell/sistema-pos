package com.example.pos.application.usecase;

import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Habilitar o deshabilitar un usuario del sistema.
 */
@Service
public class CambiarEstadoUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public CambiarEstadoUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    /**
     * Cambia el estado activo/inactivo del usuario.
     *
     * @param id     Identificador único del usuario.
     * @param activo Nuevo estado.
     * @return {@link UsuarioDomain} actualizado.
     * @throws UsuarioNoEncontradoException si no existe el usuario.
     * @throws IllegalArgumentException     si el ID es nulo.
     */
    @Transactional
    public UsuarioDomain ejecutar(Long id, boolean activo) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }

        UsuarioDomain usuario = usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> UsuarioNoEncontradoException.porId(id));

        if (activo) {
            usuario.activar();
        } else {
            usuario.desactivar();
        }

        return usuarioRepositoryPort.guardar(usuario);
    }
}
