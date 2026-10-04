package com.example.pos.application.usecase;

import com.example.pos.domain.exception.UsuarioNoEncontradoException;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar un usuario por su identificador único.
 */
@Service
public class BuscarUsuarioPorIdUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public BuscarUsuarioPorIdUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    /**
     * Recupera el usuario con el ID indicado.
     *
     * @param id Identificador único.
     * @return {@link UsuarioDomain} encontrado.
     * @throws UsuarioNoEncontradoException si no existe.
     * @throws IllegalArgumentException     si el ID es nulo.
     */
    @Transactional(readOnly = true)
    public UsuarioDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }
        return usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> UsuarioNoEncontradoException.porId(id));
    }
}
