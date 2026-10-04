package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ClienteNoEncontradoException;
import com.example.pos.domain.model.ClienteDomain;
import com.example.pos.domain.repository.ClienteRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Actualizar los datos de un cliente existente en el sistema POS.
 *
 * <p>Pertenece a la capa de Aplicación. Orquesta las siguientes responsabilidades:
 * <ol>
 *   <li>Buscar el cliente por ID a través del puerto de salida.</li>
 *   <li>Validar su existencia lanzando {@link ClienteNoEncontradoException} si no existe.</li>
 *   <li>Delegar la actualización de los campos al propio modelo de dominio
 *       (respetando sus invariantes y reglas de negocio internas).</li>
 *   <li>Persistir el cliente modificado mediante el puerto de salida.</li>
 * </ol>
 * </p>
 */
@Service
public class ActualizarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    /**
     * Inyección por constructor del puerto de salida (Driven Port).
     */
    public ActualizarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    /**
     * Ejecuta la actualización de un cliente a partir de su ID y un comando con los nuevos datos.
     *
     * @param id      Identificador único del cliente a actualizar.
     * @param command Comando que contiene los campos a modificar.
     * @return {@link ClienteDomain} con los datos actualizados y persistidos.
     * @throws ClienteNoEncontradoException si no existe un cliente con el ID proporcionado.
     * @throws IllegalArgumentException     si el comando es nulo o contiene datos inválidos.
     */
    @Transactional
    public ClienteDomain ejecutar(Long id, ActualizarClienteCommand command) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo.");
        }
        if (command == null) {
            throw new IllegalArgumentException("El comando de actualización no puede ser nulo.");
        }

        // 1. Recuperar el cliente; lanzar excepción de dominio si no existe
        ClienteDomain clienteExistente = clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> ClienteNoEncontradoException.porId(id));

        // 2. Aplicar los cambios mediante el método de dominio (respeta invariantes)
        clienteExistente.actualizarDatos(
                command.nombre(),
                command.telefono(),
                command.email(),
                command.direccion()
        );

        // 3. Actualizar el estado lógico si fue proporcionado en el comando
        if (command.estado() != null) {
            if (command.estado()) {
                clienteExistente.activar();
            } else {
                clienteExistente.desactivar();
            }
        }

        // 4. Persistir y retornar el cliente actualizado
        return clienteRepositoryPort.guardar(clienteExistente);
    }
}
