package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProveedorDuplicadoException;
import com.example.pos.domain.model.ProveedorDomain;
import com.example.pos.domain.repository.ProveedorRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Registrar un nuevo proveedor en el sistema.
 */
@Service
public class CrearProveedorUseCase {

    private final ProveedorRepositoryPort proveedorRepositoryPort;

    public CrearProveedorUseCase(ProveedorRepositoryPort proveedorRepositoryPort) {
        this.proveedorRepositoryPort = proveedorRepositoryPort;
    }

    /**
     * Registra un nuevo proveedor validando la unicidad de su RUC/DNI.
     *
     * @param command Datos del proveedor.
     * @return {@link ProveedorDomain} persistido.
     * @throws ProveedorDuplicadoException si ya existe un proveedor con el mismo RUC/DNI.
     * @throws IllegalArgumentException si el comando es nulo.
     */
    @Transactional
    public ProveedorDomain ejecutar(CrearProveedorCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de creación de proveedor no puede ser nulo.");
        }

        if (proveedorRepositoryPort.existePorRucDni(command.rucDni())) {
            throw ProveedorDuplicadoException.porDocumento(command.rucDni());
        }

        ProveedorDomain nuevoProveedor = new ProveedorDomain(
                command.nombre(),
                command.rucDni(),
                command.telefono(),
                command.email(),
                command.direccion()
        );

        return proveedorRepositoryPort.guardar(nuevoProveedor);
    }
}
