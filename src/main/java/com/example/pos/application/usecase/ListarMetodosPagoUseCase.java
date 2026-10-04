package com.example.pos.application.usecase;

import com.example.pos.domain.model.MetodoPagoDomain;
import com.example.pos.domain.repository.MetodoPagoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Listar métodos de pago registrados.
 * Puede filtrar opcionalmente por aquellos que se encuentren activos.
 */
@Service
public class ListarMetodosPagoUseCase {

    private final MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    public ListarMetodosPagoUseCase(MetodoPagoRepositoryPort metodoPagoRepositoryPort) {
        this.metodoPagoRepositoryPort = metodoPagoRepositoryPort;
    }

    /**
     * Retorna los métodos de pago.
     *
     * @param soloActivos si es {@code true}, retorna únicamente los métodos activos para cobro.
     * @return Lista de {@link MetodoPagoDomain}.
     */
    @Transactional(readOnly = true)
    public List<MetodoPagoDomain> ejecutar(boolean soloActivos) {
        if (soloActivos) {
            return metodoPagoRepositoryPort.listarActivos();
        }
        return metodoPagoRepositoryPort.listarTodos();
    }
}
