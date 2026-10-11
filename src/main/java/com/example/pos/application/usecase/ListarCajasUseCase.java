package com.example.pos.application.usecase;

import com.example.pos.domain.model.CajaDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarCajasUseCase {

    private final CajaRepositoryPort cajaRepositoryPort;

    public ListarCajasUseCase(CajaRepositoryPort cajaRepositoryPort) {
        this.cajaRepositoryPort = cajaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<CajaDomain> ejecutar() {
        return cajaRepositoryPort.listarTodas();
    }
}
