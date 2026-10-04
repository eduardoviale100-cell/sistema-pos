package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MetodoPagoDuplicadoException;
import com.example.pos.domain.exception.MetodoPagoNoEncontradoException;
import com.example.pos.domain.model.MetodoPagoDomain;
import com.example.pos.domain.repository.MetodoPagoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Actualizar los datos de un método de pago.
 */
@Service
public class ActualizarMetodoPagoUseCase {

    private final MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    public ActualizarMetodoPagoUseCase(MetodoPagoRepositoryPort metodoPagoRepositoryPort) {
        this.metodoPagoRepositoryPort = metodoPagoRepositoryPort;
    }

    /**
     * Actualiza el método de pago indicado por ID.
     *
     * @param id Identificador único.
     * @param command Datos actualizados.
     * @return {@link MetodoPagoDomain} actualizado.
     * @throws MetodoPagoNoEncontradoException si no existe.
     * @throws MetodoPagoDuplicadoException si el nuevo nombre colisiona con otro método de pago.
     * @throws IllegalArgumentException si el ID o el comando son nulos.
     */
    @Transactional
    public MetodoPagoDomain ejecutar(Long id, ActualizarMetodoPagoCommand command) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del método de pago no puede ser nulo.");
        }
        if (command == null) {
            throw new IllegalArgumentException("El comando de actualización de método de pago no puede ser nulo.");
        }

        MetodoPagoDomain metodo = metodoPagoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> MetodoPagoNoEncontradoException.porId(id));

        if (!metodo.getNombre().equalsIgnoreCase(command.nombre())) {
            metodoPagoRepositoryPort.buscarPorNombre(command.nombre())
                    .ifPresent(m -> {
                        if (!m.getId().equals(id)) {
                            throw MetodoPagoDuplicadoException.porNombre(command.nombre());
                        }
                    });
        }

        metodo.actualizarDatos(command.nombre(), command.activo());

        return metodoPagoRepositoryPort.guardar(metodo);
    }
}
