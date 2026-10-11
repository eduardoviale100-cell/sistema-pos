package com.example.pos.domain.repository;

import com.example.pos.domain.model.MovimientoCajaDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Movimientos de Caja.
 */
public interface MovimientoCajaRepositoryPort {

    MovimientoCajaDomain guardar(MovimientoCajaDomain movimiento);

    Optional<MovimientoCajaDomain> buscarPorId(Long id);

    List<MovimientoCajaDomain> listarPorSesionCajaId(Long sesionCajaId);
}
