package com.example.pos.domain.repository;

import com.example.pos.domain.model.ProveedorDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Proveedores.
 * Libre de dependencias externas y acoplamientos tecnológicos.
 */
public interface ProveedorRepositoryPort {

    /**
     * Persiste o actualiza un proveedor.
     *
     * @param proveedor Modelo de dominio a guardar.
     * @return {@link ProveedorDomain} persistido.
     */
    ProveedorDomain guardar(ProveedorDomain proveedor);

    /**
     * Busca un proveedor por su ID.
     *
     * @param id Identificador único.
     * @return Optional con el proveedor si existe.
     */
    Optional<ProveedorDomain> buscarPorId(Long id);

    /**
     * Busca un proveedor por su documento de identidad fiscal (RUC/DNI).
     *
     * @param rucDni Documento de identidad fiscal.
     * @return Optional con el proveedor si existe.
     */
    Optional<ProveedorDomain> buscarPorRucDni(String rucDni);

    /**
     * Lista todos los proveedores registrados.
     *
     * @return Lista de proveedores.
     */
    List<ProveedorDomain> listarTodos();

    /**
     * Elimina un proveedor por su identificador único.
     *
     * @param id Identificador único.
     */
    void eliminarPorId(Long id);

    /**
     * Verifica si ya existe un proveedor con el RUC/DNI especificado.
     *
     * @param rucDni Documento a verificar.
     * @return {@code true} si existe, {@code false} en caso contrario.
     */
    boolean existePorRucDni(String rucDni);
}
