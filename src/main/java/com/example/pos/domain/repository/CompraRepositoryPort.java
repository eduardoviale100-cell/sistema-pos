package com.example.pos.domain.repository;

import com.example.pos.domain.model.compra.CompraDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven Port) para la persistencia de Compras.
 */
public interface CompraRepositoryPort {

    /**
     * Persiste una compra con sus detalles.
     *
     * @param compra Agregado de compra.
     * @return {@link CompraDomain} guardada.
     */
    CompraDomain guardar(CompraDomain compra);

    /**
     * Busca una compra por su ID.
     *
     * @param id Identificador de la compra.
     * @return {@link Optional} con la compra de dominio.
     */
    Optional<CompraDomain> buscarPorId(Long id);

    /**
     * Obtiene el listado completo de compras realizadas.
     *
     * @return Lista de compras.
     */
    List<CompraDomain> listarTodas();
}
