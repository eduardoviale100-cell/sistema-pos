package com.example.pos.domain.repository;

import com.example.pos.domain.model.ProductoDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven Port / Secondary Port) para la persistencia de Producto.
 *
 * <p>Define el contrato que el adaptador de persistencia (SPI) debe implementar.
 * Esta interfaz pertenece estrictamente al dominio y está libre de dependencias
 * de infraestructura (JPA, Spring Data, etc.).</p>
 *
 * <p>Principio de Inversión de Dependencias (DIP): el dominio define este puerto y
 * la capa de infraestructura lo implementa, nunca al revés.</p>
 */
public interface ProductoRepositoryPort {

    /**
     * Persiste un nuevo producto o actualiza uno existente.
     *
     * @param producto Modelo de dominio a guardar o actualizar.
     * @return {@link ProductoDomain} persistido con su ID asignado o actualizado.
     */
    ProductoDomain guardar(ProductoDomain producto);

    /**
     * Busca un producto por su identificador único (clave técnica de la BD).
     *
     * @param id Identificador único del producto.
     * @return {@link Optional} con el {@link ProductoDomain} si existe, o vacío si no se encuentra.
     */
    Optional<ProductoDomain> buscarPorId(Long id);

    /**
     * Busca un producto por su código de barras o código interno (clave de negocio).
     *
     * @param codigo Código único del producto.
     * @return {@link Optional} con el {@link ProductoDomain} si existe, o vacío si no se encuentra.
     */
    Optional<ProductoDomain> buscarPorCodigo(String codigo);

    /**
     * Obtiene el listado completo de productos registrados en el sistema.
     *
     * @return Lista de {@link ProductoDomain}. Puede estar vacía, nunca es {@code null}.
     */
    List<ProductoDomain> listarTodos();

    /**
     * Verifica si ya existe un producto con el código especificado.
     * Útil para validar unicidad antes de persistir, evitando consultas completas.
     *
     * @param codigo Código a verificar.
     * @return {@code true} si ya existe un producto con dicho código; {@code false} en caso contrario.
     */
    boolean existePorCodigo(String codigo);
}
