package com.example.pos.domain.repository;

import com.example.pos.domain.model.MetodoPagoDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Métodos de Pago.
 */
public interface MetodoPagoRepositoryPort {

    /**
     * Persiste o actualiza un método de pago.
     *
     * @param metodoPago Modelo de dominio a guardar.
     * @return {@link MetodoPagoDomain} persistido.
     */
    MetodoPagoDomain guardar(MetodoPagoDomain metodoPago);

    /**
     * Busca un método de pago por su ID.
     *
     * @param id Identificador único.
     * @return Optional con el método de pago si existe.
     */
    Optional<MetodoPagoDomain> buscarPorId(Long id);

    /**
     * Busca un método de pago por su nombre exacto.
     *
     * @param nombre Nombre del método de pago.
     * @return Optional con el método de pago si existe.
     */
    Optional<MetodoPagoDomain> buscarPorNombre(String nombre);

    /**
     * Lista todos los métodos de pago registrados.
     *
     * @return Lista completa.
     */
    List<MetodoPagoDomain> listarTodos();

    /**
     * Lista únicamente los métodos de pago activos para operaciones de cobro en ventas.
     *
     * @return Lista de métodos activos.
     */
    List<MetodoPagoDomain> listarActivos();

    /**
     * Elimina un método de pago por su identificador único.
     *
     * @param id Identificador único.
     */
    void eliminarPorId(Long id);

    /**
     * Verifica si existe un método de pago con el nombre indicado.
     *
     * @param nombre Nombre a verificar.
     * @return {@code true} si existe, {@code false} en caso contrario.
     */
    boolean existePorNombre(String nombre);
}
