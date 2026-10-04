package com.example.pos.application.usecase;

import com.example.pos.domain.model.CategoriaDomain;
import com.example.pos.domain.repository.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Listar todas las categorías registradas.
 */
@Service
public class ListarCategoriasUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public ListarCategoriasUseCase(CategoriaRepositoryPort categoriaRepositoryPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDomain> ejecutar() {
        return categoriaRepositoryPort.listarTodas();
    }
}
