package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MetodoPagoNoEncontradoException;
import com.example.pos.domain.model.MetodoPagoDomain;
import com.example.pos.domain.repository.MetodoPagoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar un método de pago por su identificador único.
 */
@Service
public class BuscarMetodoPagoPorIdUseCase {

    private final MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    public BuscarMetodoPagoPorIdUseCase(MetodoPagoRepositoryPort metodoPagoRepositoryPort) {
        this.metodoPagoRepositoryPort = metodoPagoRepositoryPort;
    }

    @Transactional(readOnly = true)
    public MetodoPagoDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del método de pago no puede ser nulo.");
        }

        return metodoPagoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> MetodoPagoNoEncontradoException.porId(id));
    }
}
