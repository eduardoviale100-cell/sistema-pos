package com.example.pos.application.usecase.caja;

import com.example.pos.domain.model.caja.*;
import com.example.pos.domain.model.caja.*;

import com.example.pos.domain.model.caja.SesionCajaDomain;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Caso de Uso: Obtener la sesión activa de una caja específica.
 */
@Service
public class ObtenerSesionActivaUseCase {

    private final SesionCajaRepositoryPort sesionCajaRepositoryPort;

    public ObtenerSesionActivaUseCase(SesionCajaRepositoryPort sesionCajaRepositoryPort) {
        this.sesionCajaRepositoryPort = sesionCajaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public Optional<SesionCajaDomain> ejecutar(Long cajaId) {
        if (cajaId == null) {
            return Optional.empty();
        }
        return sesionCajaRepositoryPort.buscarSesionActivaPorCajaId(cajaId);
    }
}
