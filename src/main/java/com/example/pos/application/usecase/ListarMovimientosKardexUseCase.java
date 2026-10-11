package com.example.pos.application.usecase;

import com.example.pos.domain.model.MovimientoInventarioDomain;
import com.example.pos.domain.repository.InventarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Consultar el historial de movimientos de Kardex de inventario.
 */
@Service
public class ListarMovimientosKardexUseCase {

    private final InventarioRepositoryPort inventarioRepositoryPort;

    public ListarMovimientosKardexUseCase(InventarioRepositoryPort inventarioRepositoryPort) {
        this.inventarioRepositoryPort = inventarioRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioDomain> listarPorProductoId(Long productoId) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }
        return inventarioRepositoryPort.listarMovimientosPorProductoId(productoId);
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioDomain> listarTodos() {
        return inventarioRepositoryPort.listarTodosLosMovimientos();
    }
}
