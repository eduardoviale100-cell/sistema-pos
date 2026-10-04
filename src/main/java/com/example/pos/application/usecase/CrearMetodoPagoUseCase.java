package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MetodoPagoDuplicadoException;
import com.example.pos.domain.model.MetodoPagoDomain;
import com.example.pos.domain.repository.MetodoPagoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Registrar un nuevo método de pago en el sistema.
 */
@Service
public class CrearMetodoPagoUseCase {

    private final MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    public CrearMetodoPagoUseCase(MetodoPagoRepositoryPort metodoPagoRepositoryPort) {
        this.metodoPagoRepositoryPort = metodoPagoRepositoryPort;
    }

    /**
     * Registra un nuevo método de pago validando unicidad de nombre.
     *
     * @param command Datos del método de pago.
     * @return {@link MetodoPagoDomain} persistido.
     * @throws MetodoPagoDuplicadoException si ya existe un método de pago con el mismo nombre.
     * @throws IllegalArgumentException si el comando es nulo.
     */
    @Transactional
    public MetodoPagoDomain ejecutar(CrearMetodoPagoCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de creación de método de pago no puede ser nulo.");
        }

        if (metodoPagoRepositoryPort.existePorNombre(command.nombre())) {
            throw MetodoPagoDuplicadoException.porNombre(command.nombre());
        }

        MetodoPagoDomain nuevoMetodo = new MetodoPagoDomain(
                command.nombre(),
                command.activo()
        );

        return metodoPagoRepositoryPort.guardar(nuevoMetodo);
    }
}
