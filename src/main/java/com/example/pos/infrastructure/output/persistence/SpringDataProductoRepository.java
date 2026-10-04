package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link ProductoEntity}.
 *
 * <p>Provee las operaciones CRUD estándar a través de {@link JpaRepository}
 * y las consultas derivadas (derived queries) necesarias para el módulo de Productos.</p>
 *
 * <p>Esta interfaz pertenece a la capa de Infraestructura y es utilizada únicamente
 * por {@link ProductoRepositoryAdapter}, que actúa como puente entre este repositorio
 * de Spring Data y el puerto de dominio {@code ProductoRepositoryPort}.</p>
 */
@Repository
public interface SpringDataProductoRepository extends JpaRepository<ProductoEntity, Long> {

    /**
     * Busca una entidad de producto por su código de barras o código interno.
     * Implementado automáticamente por Spring Data JPA a partir del nombre del método.
     *
     * @param codigoBarras Código único del producto a buscar.
     * @return {@link Optional} con la entidad si existe, o vacío si no se encuentra.
     */
    Optional<ProductoEntity> findByCodigoBarras(String codigoBarras);

    /**
     * Verifica la existencia de un producto por su código de barras o código interno.
     * Útil para validar unicidad antes de persistir sin traer la entidad completa.
     *
     * @param codigoBarras Código único a verificar.
     * @return {@code true} si ya existe un producto con ese código; {@code false} en caso contrario.
     */
    boolean existsByCodigoBarras(String codigoBarras);
}
