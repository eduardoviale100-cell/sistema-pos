package com.example.pos.application.usecase;

import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Eliminar un usuario del sistema por su identificador único.
 */
@Service
public class EliminarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public EliminarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    /**
     * Elimina el usuario con el ID indicado.
     *
     * @param id Identificador único.
     * @throws UsuarioNoEncontradoException si no existe.
     * @throws IllegalArgumentException     si el ID es nulo.
     */
    @Transactional
    public void ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }

        usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> UsuarioNoEncontradoException.porId(id));

        usuarioRepositoryPort.eliminarPorId(id);
    }
}
