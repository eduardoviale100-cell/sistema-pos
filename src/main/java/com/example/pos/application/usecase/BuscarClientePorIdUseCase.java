package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ClienteNoEncontradoException;
import com.example.pos.domain.model.ClienteDomain;
import com.example.pos.domain.repository.ClienteRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar un cliente específico por su identificador único.
 *
 * <p>Pertenece a la capa de Aplicación. Lanza {@link ClienteNoEncontradoException}
 * si el ID no corresponde a ningún cliente registrado en el sistema.</p>
 */
@Service
public class BuscarClientePorIdUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public BuscarClientePorIdUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    /**
     * Ejecuta la búsqueda de un cliente por su ID.
     *
     * @param id Identificador único del cliente.
     * @return {@link ClienteDomain} encontrado.
     * @throws ClienteNoEncontradoException si no existe el cliente.
     * @throws IllegalArgumentException si el ID es nulo.
     */
    @Transactional(readOnly = true)
    public ClienteDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo.");
        }

        return clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> ClienteNoEncontradoException.porId(id));
    }
}
