package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MarcaNoEncontradaException;
import com.example.pos.domain.model.MarcaDomain;
import com.example.pos.domain.repository.MarcaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Eliminar una marca por su identificador.
 */
@Service
public class EliminarMarcaUseCase {

    private final MarcaRepositoryPort marcaRepositoryPort;

    public EliminarMarcaUseCase(MarcaRepositoryPort marcaRepositoryPort) {
        this.marcaRepositoryPort = marcaRepositoryPort;
    }

    /**
     * Elimina la marca si existe.
     *
     * @param id Identificador único de la marca.
     * @throws MarcaNoEncontradaException si no existe.
     * @throws IllegalArgumentException si el ID es nulo.
     */
    @Transactional
    public void ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la marca no puede ser nulo.");
        }

        if (marcaRepositoryPort.buscarPorId(id).isEmpty()) {
            throw MarcaNoEncontradaException.porId(id);
        }

        marcaRepositoryPort.eliminarPorId(id);
    }
}
