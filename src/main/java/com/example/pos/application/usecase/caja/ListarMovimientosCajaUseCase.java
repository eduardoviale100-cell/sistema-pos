package com.example.pos.application.usecase.caja;

import com.example.pos.domain.model.caja.*;
import com.example.pos.domain.model.caja.*;

import com.example.pos.domain.model.caja.MovimientoCajaDomain;
import com.example.pos.domain.repository.MovimientoCajaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarMovimientosCajaUseCase {

    private final MovimientoCajaRepositoryPort movimientoCajaRepositoryPort;

    public ListarMovimientosCajaUseCase(MovimientoCajaRepositoryPort movimientoCajaRepositoryPort) {
        this.movimientoCajaRepositoryPort = movimientoCajaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<MovimientoCajaDomain> ejecutar(Long sesionId) {
        if (sesionId == null) {
            return List.of();
        }
        return movimientoCajaRepositoryPort.listarPorSesionCajaId(sesionId);
    }
}
