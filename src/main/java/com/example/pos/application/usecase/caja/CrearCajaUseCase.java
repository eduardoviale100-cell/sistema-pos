package com.example.pos.application.usecase.caja;

import com.example.pos.domain.model.caja.*;
import com.example.pos.domain.model.caja.*;

import com.example.pos.domain.model.caja.CajaDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearCajaUseCase {

    private final CajaRepositoryPort cajaRepositoryPort;

    public CrearCajaUseCase(CajaRepositoryPort cajaRepositoryPort) {
        this.cajaRepositoryPort = cajaRepositoryPort;
    }

    @Transactional
    public CajaDomain ejecutar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la caja no puede estar vacío.");
        }
        String nombreNormalizado = nombre.trim();
        if (cajaRepositoryPort.existePorNombre(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe una caja registrada con el nombre: " + nombreNormalizado);
        }
        CajaDomain nuevaCaja = new CajaDomain(null, nombreNormalizado, "CERRADA");
        return cajaRepositoryPort.guardar(nuevaCaja);
    }
}
