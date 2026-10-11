package com.example.pos.domain.repository;

import com.example.pos.domain.model.CajaDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Cajas.
 */
public interface CajaRepositoryPort {

    CajaDomain guardar(CajaDomain caja);

    Optional<CajaDomain> buscarPorId(Long id);

    Optional<CajaDomain> buscarPorNombre(String nombre);

    List<CajaDomain> listarTodas();

    void eliminarPorId(Long id);

    boolean existePorNombre(String nombre);

    boolean existePorNombreExcluyendoId(String nombre, Long idExcluido);
}
