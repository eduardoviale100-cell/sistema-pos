package com.example.pos.application.usecase;

import com.example.pos.domain.exception.CategoriaNoEncontradaException;
import com.example.pos.domain.model.CategoriaDomain;
import com.example.pos.domain.repository.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Eliminar una categoría por su identificador.
 */
@Service
public class EliminarCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public EliminarCategoriaUseCase(CategoriaRepositoryPort categoriaRepositoryPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    /**
     * Elimina la categoría si existe.
     *
     * @param id Identificador único de la categoría.
     * @throws CategoriaNoEncontradaException si no existe.
     * @throws IllegalArgumentException si el ID es nulo.
     */
    @Transactional
    public void ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo.");
        }

        if (categoriaRepositoryPort.buscarPorId(id).isEmpty()) {
            throw CategoriaNoEncontradaException.porId(id);
        }

        categoriaRepositoryPort.eliminarPorId(id);
    }
}
