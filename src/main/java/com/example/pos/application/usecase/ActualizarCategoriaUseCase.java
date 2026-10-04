package com.example.pos.application.usecase;

import com.example.pos.domain.exception.CategoriaDuplicadaException;
import com.example.pos.domain.exception.CategoriaNoEncontradaException;
import com.example.pos.domain.model.CategoriaDomain;
import com.example.pos.domain.repository.CategoriaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Actualizar los datos de una categoría existente.
 */
@Service
public class ActualizarCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;

    public ActualizarCategoriaUseCase(CategoriaRepositoryPort categoriaRepositoryPort) {
        this.categoriaRepositoryPort = categoriaRepositoryPort;
    }

    /**
     * Actualiza la categoría indicada por ID.
     *
     * @param id Identificador único de la categoría.
     * @param command Datos actualizados.
     * @return {@link CategoriaDomain} actualizado.
     * @throws CategoriaNoEncontradaException si no existe la categoría.
     * @throws CategoriaDuplicadaException si el nuevo nombre ya pertenece a otra categoría.
     * @throws IllegalArgumentException si el ID o el comando son nulos.
     */
    @Transactional
    public CategoriaDomain ejecutar(Long id, ActualizarCategoriaCommand command) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo.");
        }
        if (command == null) {
            throw new IllegalArgumentException("El comando de actualización de categoría no puede ser nulo.");
        }

        CategoriaDomain categoria = categoriaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> CategoriaNoEncontradaException.porId(id));

        // Si el nombre cambia, verificar que no colisione con otra categoría
        if (!categoria.getNombre().equalsIgnoreCase(command.nombre())) {
            categoriaRepositoryPort.buscarPorNombre(command.nombre())
                    .ifPresent(c -> {
                        if (!c.getId().equals(id)) {
                            throw CategoriaDuplicadaException.porNombre(command.nombre());
                        }
                    });
        }

        categoria.actualizarDatos(command.nombre(), command.descripcion());

        return categoriaRepositoryPort.guardar(categoria);
    }
}
