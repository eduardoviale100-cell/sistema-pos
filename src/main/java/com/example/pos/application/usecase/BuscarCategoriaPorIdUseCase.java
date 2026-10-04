package com.example.pos.application.usecase;

import com.example.pos.domain.exception.CategoriaNoEncontradaException;
import com.example.pos.domain.model.CategoriaDomain;
import com.example.pos.domain.repository.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar una categoría por su identificador único.
 */
@Service
public class BuscarCategoriaPorIdUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public BuscarCategoriaPorIdUseCase(CategoriaRepositoryPort categoriaRepositoryPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public CategoriaDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo.");
        }

        return categoriaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> CategoriaNoEncontradaException.porId(id));
    }
}
