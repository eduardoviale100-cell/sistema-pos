package com.example.pos.infrastructure.output.persistence.producto;


import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link ProductoEntity}.
 *
 * <p>Provee las operaciones CRUD estÃ¡ndar a travÃ©s de {@link JpaRepository}
 * y las consultas derivadas (derived queries) necesarias para el mÃ³dulo de Productos.</p>
 *
 * <p>Esta interfaz pertenece a la capa de Infraestructura y es utilizada Ãºnicamente
 * por {@link ProductoRepositoryAdapter}, que actÃºa como puente entre este repositorio
 * de Spring Data y el puerto de dominio {@code ProductoRepositoryPort}.</p>
 */
@Repository
public interface SpringDataProductoRepository extends JpaRepository<ProductoEntity, Long> {

    @EntityGraph(attributePaths = {"categoria", "marca", "proveedor"})
    List<ProductoEntity> findAll();

    @EntityGraph(attributePaths = {"categoria", "marca", "proveedor"})
    Optional<ProductoEntity> findById(Long id);

    /**
     * Busca una entidad de producto por su cÃ³digo de barras o cÃ³digo interno.
     * Implementado automÃ¡ticamente por Spring Data JPA a partir del nombre del mÃ©todo.
     *
     * @param codigoBarras CÃ³digo Ãºnico del producto a buscar.
     * @return {@link Optional} con la entidad si existe, o vacÃ­o si no se encuentra.
     */
    Optional<ProductoEntity> findByCodigoBarras(String codigoBarras);

    /**
     * Verifica la existencia de un producto por su cÃ³digo de barras o cÃ³digo interno.
     * Ãštil para validar unicidad antes de persistir sin traer la entidad completa.
     *
     * @param codigoBarras CÃ³digo Ãºnico a verificar.
     * @return {@code true} si ya existe un producto con ese cÃ³digo; {@code false} en caso contrario.
     */
    boolean existsByCodigoBarras(String codigoBarras);
}

