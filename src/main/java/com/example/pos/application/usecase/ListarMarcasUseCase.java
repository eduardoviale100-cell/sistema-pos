package com.example.pos.application.usecase;

import com.example.pos.domain.model.MarcaDomain;
import com.example.pos.domain.repository.MarcaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Listar todas las marcas registradas.
 */
@Service
public class ListarMarcasUseCase {

    private final MarcaRepositoryPort marcaRepositoryPort;

    public ListarMarcasUseCase(MarcaRepositoryPort marcaRepositoryPort) {
        this.marcaRepositoryPort = marcaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<MarcaDomain> ejecutar() {
        return marcaRepositoryPort.listarTodas();
    }
}
