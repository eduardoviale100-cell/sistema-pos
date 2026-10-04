package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MarcaDuplicadaException;
import com.example.pos.domain.model.MarcaDomain;
import com.example.pos.domain.repository.MarcaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Registrar una nueva marca en el sistema.
 */
@Service
public class CrearMarcaUseCase {

    private final MarcaRepositoryPort marcaRepositoryPort;

    public CrearMarcaUseCase(MarcaRepositoryPort marcaRepositoryPort) {
        this.marcaRepositoryPort = marcaRepositoryPort;
    }

    /**
     * Ejecuta el registro de una nueva marca.
     *
     * @param command Datos de la marca a crear.
     * @return {@link MarcaDomain} persistido.
     * @throws MarcaDuplicadaException si ya existe una marca con el mismo nombre.
     * @throws IllegalArgumentException si el comando es nulo.
     */
    @Transactional
    public MarcaDomain ejecutar(CrearMarcaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de creación de marca no puede ser nulo.");
        }

        if (marcaRepositoryPort.existePorNombre(command.nombre())) {
            throw MarcaDuplicadaException.porNombre(command.nombre());
        }

        MarcaDomain nuevaMarca = new MarcaDomain(
                command.nombre(),
                command.descripcion()
        );

        return marcaRepositoryPort.guardar(nuevaMarca);
    }
}
