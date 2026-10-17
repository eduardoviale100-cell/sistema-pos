package com.example.pos.domain.repository;

import com.example.pos.domain.model.caja.SesionCajaDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Sesiones de Caja.
 */
public interface SesionCajaRepositoryPort {

    SesionCajaDomain guardar(SesionCajaDomain sesion);

    Optional<SesionCajaDomain> buscarPorId(Long sesionId);

    Optional<SesionCajaDomain> buscarSesionActivaPorCajaId(Long cajaId);

    List<SesionCajaDomain> listarPorCajaId(Long cajaId);

    boolean existeSesionAbierta(Long cajaId);
 
    List<SesionCajaDomain> buscarSesionesAbiertasPorCajaId(Long cajaId);

    List<SesionCajaDomain> buscarSesionesAbiertasPorUsuarioId(Long usuarioId);
}
