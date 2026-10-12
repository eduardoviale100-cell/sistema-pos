package com.example.pos.application.usecase.caja;

import com.example.pos.domain.model.caja.*;
import com.example.pos.domain.model.caja.*;

import com.example.pos.domain.exception.CajaNoEncontradaException;
import com.example.pos.domain.model.caja.CajaDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarCajaUseCase {

    private final CajaRepositoryPort cajaRepositoryPort;

    public EliminarCajaUseCase(CajaRepositoryPort cajaRepositoryPort) {
        this.cajaRepositoryPort = cajaRepositoryPort;
    }

    @Transactional
    public void ejecutar(Long id) {
        CajaDomain caja = cajaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> CajaNoEncontradaException.porId(id));

        if (caja.isAbierta()) {
            throw new IllegalStateException("No se puede eliminar una caja con sesión ABIERTA.");
        }

        cajaRepositoryPort.eliminarPorId(id);
    }
}
