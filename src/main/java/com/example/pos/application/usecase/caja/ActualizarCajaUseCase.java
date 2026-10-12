package com.example.pos.application.usecase.caja;

import com.example.pos.domain.model.caja.*;
import com.example.pos.domain.model.caja.*;

import com.example.pos.domain.exception.CajaNoEncontradaException;
import com.example.pos.domain.model.caja.CajaDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarCajaUseCase {

    private final CajaRepositoryPort cajaRepositoryPort;

    public ActualizarCajaUseCase(CajaRepositoryPort cajaRepositoryPort) {
        this.cajaRepositoryPort = cajaRepositoryPort;
    }

    @Transactional
    public CajaDomain ejecutar(Long id, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la caja no puede estar vacío.");
        }

        CajaDomain caja = cajaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> CajaNoEncontradaException.porId(id));

        String nombreNormalizado = nuevoNombre.trim();
        if (cajaRepositoryPort.existePorNombreExcluyendoId(nombreNormalizado, id)) {
            throw new IllegalArgumentException("Ya existe otra caja con el nombre: " + nombreNormalizado);
        }

        caja.setNombre(nombreNormalizado);
        return cajaRepositoryPort.guardar(caja);
    }
}
