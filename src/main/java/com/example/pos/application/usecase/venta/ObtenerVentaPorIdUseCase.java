package com.example.pos.application.usecase.venta;

import com.example.pos.domain.exception.VentaNoEncontradaException;
import com.example.pos.domain.model.venta.VentaDomain;
import com.example.pos.domain.repository.VentaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar una venta por su identificador único.
 */
@Service
public class ObtenerVentaPorIdUseCase {

    private final VentaRepositoryPort ventaRepositoryPort;

    public ObtenerVentaPorIdUseCase(VentaRepositoryPort ventaRepositoryPort) {
        this.ventaRepositoryPort = ventaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public VentaDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la venta no puede ser nulo.");
        }
        return ventaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> VentaNoEncontradaException.porId(id));
    }
}
