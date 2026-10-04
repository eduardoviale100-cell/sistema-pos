package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProveedorNoEncontradoException;
import com.example.pos.domain.repository.ProveedorRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Eliminar un proveedor por su identificador único.
 */
@Service
public class EliminarProveedorUseCase {

    private final ProveedorRepositoryPort proveedorRepositoryPort;

    public EliminarProveedorUseCase(ProveedorRepositoryPort proveedorRepositoryPort) {
        this.proveedorRepositoryPort = proveedorRepositoryPort;
    }

    /**
     * Elimina el proveedor si existe.
     *
     * @param id Identificador único del proveedor.
     * @throws ProveedorNoEncontradoException si no existe.
     * @throws IllegalArgumentException si el ID es nulo.
     */
    @Transactional
    public void ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del proveedor no puede ser nulo.");
        }

        if (proveedorRepositoryPort.buscarPorId(id).isEmpty()) {
            throw ProveedorNoEncontradoException.porId(id);
        }

        proveedorRepositoryPort.eliminarPorId(id);
    }
}
