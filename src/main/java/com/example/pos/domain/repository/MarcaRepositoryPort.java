package com.example.pos.domain.repository;

import com.example.pos.domain.model.MarcaDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Marcas.
 */
public interface MarcaRepositoryPort {

    /**
     * Persiste una nueva marca o actualiza una existente.
     *
     * @param marca Modelo de dominio a guardar.
     * @return {@link MarcaDomain} persistido.
     */
    MarcaDomain guardar(MarcaDomain marca);

    /**
     * Busca una marca por su identificador único.
     *
     * @param id Identificador único de la marca.
     * @return Optional con la marca encontrada o vacío.
     */
    Optional<MarcaDomain> buscarPorId(Long id);

    /**
     * Busca una marca por su nombre exacto.
     *
     * @param nombre Nombre de la marca.
     * @return Optional con la marca encontrada o vacío.
     */
    Optional<MarcaDomain> buscarPorNombre(String nombre);

    /**
     * Lista todas las marcas registradas.
     *
     * @return Lista de marcas.
     */
    List<MarcaDomain> listarTodas();

    /**
     * Elimina una marca por su identificador único.
     *
     * @param id Identificador único de la marca.
     */
    void eliminarPorId(Long id);

    /**
     * Verifica si existe una marca con el nombre indicado.
     *
     * @param nombre Nombre a verificar.
     * @return {@code true} si ya existe, {@code false} en caso contrario.
     */
    boolean existePorNombre(String nombre);
}
