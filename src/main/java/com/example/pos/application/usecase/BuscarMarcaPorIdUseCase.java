package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MarcaNoEncontradaException;
import com.example.pos.domain.model.MarcaDomain;
import com.example.pos.domain.repository.MarcaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar una marca por su identificador único.
 */
@Service
public class BuscarMarcaPorIdUseCase {

    private final MarcaRepositoryPort marcaRepositoryPort;

    public BuscarMarcaPorIdUseCase(MarcaRepositoryPort marcaRepositoryPort) {
        this.marcaRepositoryPort = marcaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public MarcaDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la marca no puede ser nulo.");
        }

        return marcaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> MarcaNoEncontradaException.porId(id));
    }
}
