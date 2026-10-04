package com.example.pos.domain.repository;

import com.example.pos.domain.model.CategoriaDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Categorías.
 *
 * <p>Define el contrato que el adaptador de persistencia debe implementar.
 * Esta interfaz pertenece estrictamente al dominio y está libre de dependencias
 * de frameworks (JPA, Spring Data, etc.).</p>
 */
public interface CategoriaRepositoryPort {

    /**
     * Persiste una nueva categoría o actualiza una existente.
     *
     * @param categoria Modelo de dominio a guardar.
     * @return {@link CategoriaDomain} persistido.
     */
    CategoriaDomain guardar(CategoriaDomain categoria);

    /**
     * Busca una categoría por su identificador único.
     *
     * @param id Identificador único de la categoría.
     * @return Optional con la categoría encontrada o vacío.
     */
    Optional<CategoriaDomain> buscarPorId(Long id);

    /**
     * Busca una categoría por su nombre exacto.
     *
     * @param nombre Nombre de la categoría.
     * @return Optional con la categoría encontrada o vacío.
     */
    Optional<CategoriaDomain> buscarPorNombre(String nombre);

    /**
     * Lista todas las categorías registradas.
     *
     * @return Lista de categorías.
     */
    List<CategoriaDomain> listarTodas();

    /**
     * Elimina una categoría por su identificador único.
     *
     * @param id Identificador único de la categoría.
     */
    void eliminarPorId(Long id);

    /**
     * Verifica si existe una categoría con el nombre indicado.
     *
     * @param nombre Nombre a verificar.
     * @return {@code true} si ya existe, {@code false} en caso contrario.
     */
    boolean existePorNombre(String nombre);
}
