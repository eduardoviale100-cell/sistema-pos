package com.example.pos.application.usecase;

import com.example.pos.domain.model.CompraDomain;
import com.example.pos.domain.repository.CompraRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Listar el historial completo de compras registradas.
 */
@Service
public class ListarComprasUseCase {

    private final CompraRepositoryPort compraRepositoryPort;

    public ListarComprasUseCase(CompraRepositoryPort compraRepositoryPort) {
        this.compraRepositoryPort = compraRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<CompraDomain> ejecutar() {
        return compraRepositoryPort.listarTodas();
    }
}
