package com.example.pos.application.usecase;

import com.example.pos.domain.exception.CompraNoEncontradaException;
import com.example.pos.domain.model.CompraDomain;
import com.example.pos.domain.repository.CompraRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar una compra por su identificador único.
 */
@Service
public class ObtenerCompraPorIdUseCase {

    private final CompraRepositoryPort compraRepositoryPort;

    public ObtenerCompraPorIdUseCase(CompraRepositoryPort compraRepositoryPort) {
        this.compraRepositoryPort = compraRepositoryPort;
    }

    @Transactional(readOnly = true)
    public CompraDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la compra es obligatorio.");
        }
        return compraRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> CompraNoEncontradaException.porId(id));
    }
}
