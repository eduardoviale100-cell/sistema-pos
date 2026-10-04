package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MarcaDuplicadaException;
import com.example.pos.domain.exception.MarcaNoEncontradaException;
import com.example.pos.domain.model.MarcaDomain;
import com.example.pos.domain.repository.MarcaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Actualizar los datos de una marca existente.
 */
@Service
public class ActualizarMarcaUseCase {

    private final MarcaRepositoryPort marcaRepositoryPort;

    public ActualizarMarcaUseCase(MarcaRepositoryPort marcaRepositoryPort) {
        this.marcaRepositoryPort = marcaRepositoryPort;
    }

    /**
     * Actualiza la marca indicada por ID.
     *
     * @param id Identificador único de la marca.
     * @param command Datos actualizados.
     * @return {@link MarcaDomain} actualizado.
     * @throws MarcaNoEncontradaException si no existe la marca.
     * @throws MarcaDuplicadaException si el nuevo nombre ya pertenece a otra marca.
     * @throws IllegalArgumentException si el ID o el comando son nulos.
     */
    @Transactional
    public MarcaDomain ejecutar(Long id, ActualizarMarcaCommand command) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la marca no puede ser nulo.");
        }
        if (command == null) {
            throw new IllegalArgumentException("El comando de actualización de marca no puede ser nulo.");
        }

        MarcaDomain marca = marcaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> MarcaNoEncontradaException.porId(id));

        // Si el nombre cambia, verificar que no colisione con otra marca
        if (!marca.getNombre().equalsIgnoreCase(command.nombre())) {
            marcaRepositoryPort.buscarPorNombre(command.nombre())
                    .ifPresent(m -> {
                        if (!m.getId().equals(id)) {
                            throw MarcaDuplicadaException.porNombre(command.nombre());
                        }
                    });
        }

        marca.actualizarDatos(command.nombre(), command.descripcion());

        return marcaRepositoryPort.guardar(marca);
    }
}
