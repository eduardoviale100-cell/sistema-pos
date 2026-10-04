package com.example.pos.application.usecase;

import com.example.pos.domain.exception.CategoriaDuplicadaException;
import com.example.pos.domain.model.CategoriaDomain;
import com.example.pos.domain.repository.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Registrar una nueva categoría en el sistema.
 */
@Service
public class CrearCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public CrearCategoriaUseCase(CategoriaRepositoryPort categoriaRepositoryPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    /**
     * Ejecuta el registro de una nueva categoría.
     *
     * @param command Datos de la categoría a crear.
     * @return {@link CategoriaDomain} persistido.
     * @throws CategoriaDuplicadaException si ya existe una categoría con el mismo nombre.
     * @throws IllegalArgumentException si el comando es nulo.
     */
    @Transactional
    public CategoriaDomain ejecutar(CrearCategoriaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de creación de categoría no puede ser nulo.");
        }

        if (categoriaRepositoryPort.existePorNombre(command.nombre())) {
            throw CategoriaDuplicadaException.porNombre(command.nombre());
        }

        CategoriaDomain nuevaCategoria = new CategoriaDomain(
                command.nombre(),
                command.descripcion()
        );

        return categoriaRepositoryPort.guardar(nuevaCategoria);
    }
}
