package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProveedorNoEncontradoException;
import com.example.pos.domain.model.ProveedorDomain;
import com.example.pos.domain.repository.ProveedorRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar un proveedor específico por su identificador único.
 */
@Service
public class BuscarProveedorPorIdUseCase {

    private final ProveedorRepositoryPort proveedorRepositoryPort;

    public BuscarProveedorPorIdUseCase(ProveedorRepositoryPort proveedorRepositoryPort) {
        this.proveedorRepositoryPort = proveedorRepositoryPort;
    }

    @Transactional(readOnly = true)
    public ProveedorDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del proveedor no puede ser nulo.");
        }

        return proveedorRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> ProveedorNoEncontradoException.porId(id));
    }
}
