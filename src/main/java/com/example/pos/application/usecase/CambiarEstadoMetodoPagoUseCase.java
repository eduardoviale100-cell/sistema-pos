package com.example.pos.application.usecase;

import com.example.pos.domain.exception.MetodoPagoNoEncontradoException;
import com.example.pos.domain.model.MetodoPagoDomain;
import com.example.pos.domain.repository.MetodoPagoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Habilitar o inhabilitar un método de pago.
 */
@Service
public class CambiarEstadoMetodoPagoUseCase {

    private final MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    public CambiarEstadoMetodoPagoUseCase(MetodoPagoRepositoryPort metodoPagoRepositoryPort) {
        this.metodoPagoRepositoryPort = metodoPagoRepositoryPort;
    }

    /**
     * Modifica el estado activo/inactivo del método de pago.
     *
     * @param id Identificador único.
     * @param activo Nuevo estado.
     * @return {@link MetodoPagoDomain} con el estado actualizado.
     * @throws MetodoPagoNoEncontradoException si no existe.
     * @throws IllegalArgumentException si el ID es nulo.
     */
    @Transactional
    public MetodoPagoDomain ejecutar(Long id, boolean activo) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del método de pago no puede ser nulo.");
        }

        MetodoPagoDomain metodo = metodoPagoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> MetodoPagoNoEncontradoException.porId(id));

        if (activo) {
            metodo.activar();
        } else {
            metodo.desactivar();
        }

        return metodoPagoRepositoryPort.guardar(metodo);
    }
}
