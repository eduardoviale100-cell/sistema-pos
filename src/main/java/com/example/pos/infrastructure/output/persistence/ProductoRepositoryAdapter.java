package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.ProductoDomain;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia (Secondary/Driven Adapter) para el módulo de Producto.
 *
 * <p>Implementa el puerto de salida del dominio {@link ProductoRepositoryPort},
 * interactuando con Spring Data JPA y transformando datos entre el modelo de
 * dominio ({@link ProductoDomain}) y la entidad de base de datos ({@link ProductoEntity}).</p>
 *
 * <p>Responsabilidades exclusivas de este adaptador:
 * <ul>
 *   <li>Delegar operaciones de persistencia al repositorio de Spring Data JPA.</li>
 *   <li>Mapear bidireccionalente entre {@link ProductoDomain} ↔ {@link ProductoEntity}.</li>
 *   <li>Nunca contener lógica de negocio — eso es responsabilidad del dominio.</li>
 * </ul>
 * </p>
 */
@Repository
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final SpringDataProductoRepository springDataProductoRepository;

    /**
     * Inyección por constructor del repositorio de Spring Data JPA.
     */
    public ProductoRepositoryAdapter(SpringDataProductoRepository springDataProductoRepository) {
        this.springDataProductoRepository = springDataProductoRepository;
    }

    // =========================================================================
    // Implementación del Puerto de Salida
    // =========================================================================

    @Override
    public ProductoDomain guardar(ProductoDomain producto) {
        ProductoEntity entity       = toEntity(producto);
        ProductoEntity entityGuardada = springDataProductoRepository.save(entity);
        return toDomain(entityGuardada);
    }

    @Override
    public Optional<ProductoDomain> buscarPorId(Long id) {
        return springDataProductoRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<ProductoDomain> buscarPorCodigo(String codigo) {
        return springDataProductoRepository.findByCodigoBarras(codigo)
                .map(this::toDomain);
    }

    @Override
    public List<ProductoDomain> listarTodos() {
        return springDataProductoRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return springDataProductoRepository.existsByCodigoBarras(codigo);
    }

    // =========================================================================
    // Métodos de Mapeo Bidireccional (Domain <-> Entity)
    // =========================================================================

    /**
     * Convierte una entidad JPA en un modelo de dominio puro.
     * Se usa al leer datos desde la base de datos hacia la capa de aplicación.
     */
    private ProductoDomain toDomain(ProductoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ProductoDomain(
                entity.getId(),
                entity.getCodigoBarras(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrecioVenta(),
                entity.getStock(),
                entity.getEstado()
        );
    }

    /**
     * Convierte un modelo de dominio en una entidad JPA lista para persistir.
     * Se usa al escribir datos desde la capa de dominio hacia la base de datos.
     */
    private ProductoEntity toEntity(ProductoDomain domain) {
        if (domain == null) {
            return null;
        }
        return new ProductoEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getCodigo(),
                domain.getDescripcion(),
                domain.getPrecioVenta(),
                domain.getStock(),
                domain.getEstado()
        );
    }
}
