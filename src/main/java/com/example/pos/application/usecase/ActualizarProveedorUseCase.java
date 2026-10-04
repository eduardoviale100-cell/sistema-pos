package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProveedorNoEncontradoException;
import com.example.pos.domain.model.ProveedorDomain;
import com.example.pos.domain.repository.ProveedorRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Actualizar los datos de un proveedor existente.
 */
@Service
public class ActualizarProveedorUseCase {

    private final ProveedorRepositoryPort proveedorRepositoryPort;

    public ActualizarProveedorUseCase(ProveedorRepositoryPort proveedorRepositoryPort) {
        this.proveedorRepositoryPort = proveedorRepositoryPort;
    }

    /**
     * Actualiza la información de un proveedor.
     *
     * @param id Identificador único del proveedor.
     * @param command Datos actualizados.
     * @return {@link ProveedorDomain} actualizado.
     * @throws ProveedorNoEncontradoException si no existe el proveedor.
     * @throws IllegalArgumentException si el ID o comando son nulos.
     */
    @Transactional
    public ProveedorDomain ejecutar(Long id, ActualizarProveedorCommand command) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del proveedor no puede ser nulo.");
        }
        if (command == null) {
            throw new IllegalArgumentException("El comando de actualización de proveedor no puede ser nulo.");
        }

        ProveedorDomain proveedor = proveedorRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> ProveedorNoEncontradoException.porId(id));

        proveedor.actualizarDatos(
                command.nombre(),
                command.telefono(),
                command.email(),
                command.direccion()
        );

        return proveedorRepositoryPort.guardar(proveedor);
    }
}
