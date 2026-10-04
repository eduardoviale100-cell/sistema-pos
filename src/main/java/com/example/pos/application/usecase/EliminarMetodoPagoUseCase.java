package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MetodoPagoNoEncontradoException;
import com.example.pos.domain.repository.MetodoPagoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Eliminar un método de pago por su identificador único.
 */
@Service
public class EliminarMetodoPagoUseCase {

    private final MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    public EliminarMetodoPagoUseCase(MetodoPagoRepositoryPort metodoPagoRepositoryPort) {
        this.metodoPagoRepositoryPort = metodoPagoRepositoryPort;
    }

    @Transactional
    public void ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del método de pago no puede ser nulo.");
        }

        // Verificar existencia antes de eliminar
        metodoPagoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> MetodoPagoNoEncontradoException.porId(id));

        metodoPagoRepositoryPort.eliminarPorId(id);
    }
}
