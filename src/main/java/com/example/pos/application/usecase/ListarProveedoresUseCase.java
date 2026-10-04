package com.example.pos.application.usecase;

import com.example.pos.domain.model.ProveedorDomain;
import com.example.pos.domain.repository.ProveedorRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Listar todos los proveedores registrados.
 */
@Service
public class ListarProveedoresUseCase {

    private final ProveedorRepositoryPort proveedorRepositoryPort;

    public ListarProveedoresUseCase(ProveedorRepositoryPort proveedorRepositoryPort) {
        this.proveedorRepositoryPort = proveedorRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<ProveedorDomain> ejecutar() {
        return proveedorRepositoryPort.listarTodos();
    }
}
