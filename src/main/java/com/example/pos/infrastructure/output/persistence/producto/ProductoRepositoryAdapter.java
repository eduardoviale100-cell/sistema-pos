package com.example.pos.infrastructure.output.persistence.producto;


import com.example.pos.infrastructure.output.persistence.categoria.SpringDataCategoriaRepository;
import com.example.pos.infrastructure.output.persistence.proveedor.SpringDataProveedorRepository;
import com.example.pos.infrastructure.output.persistence.marca.SpringDataMarcaRepository;
import com.example.pos.domain.model.producto.ProductoDomain;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.example.pos.domain.model.categoria.CategoriaDomain;
import com.example.pos.domain.model.marca.MarcaDomain;
import com.example.pos.domain.model.proveedor.ProveedorDomain;

/**
 * Adaptador de Persistencia (Secondary/Driven Adapter) para el mÃ³dulo de Producto.
 *
 * <p>Implementa el puerto de salida del dominio {@link ProductoRepositoryPort},
 * interactuando con Spring Data JPA y transformando datos entre el modelo de
 * dominio ({@link ProductoDomain}) y la entidad de base de datos ({@link ProductoEntity}).</p>
 *
 * <p>Responsabilidades exclusivas de este adaptador:
 * <ul>
 *   <li>Delegar operaciones de persistencia al repositorio de Spring Data JPA.</li>
 *   <li>Mapear bidireccionalente entre {@link ProductoDomain} â†” {@link ProductoEntity}.</li>
 *   <li>Nunca contener lÃ³gica de negocio â€” eso es responsabilidad del dominio.</li>
 * </ul>
 * </p>
 */
@Repository
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final SpringDataProductoRepository springDataProductoRepository;
    private final SpringDataCategoriaRepository springDataCategoriaRepository;
    private final SpringDataMarcaRepository springDataMarcaRepository;
    private final SpringDataProveedorRepository springDataProveedorRepository;

    /**
     * InyecciÃ³n por constructor del repositorio de Spring Data JPA.
     */
    public ProductoRepositoryAdapter(
            SpringDataProductoRepository springDataProductoRepository,
            SpringDataCategoriaRepository springDataCategoriaRepository,
            SpringDataMarcaRepository springDataMarcaRepository,
            SpringDataProveedorRepository springDataProveedorRepository) {
        this.springDataProductoRepository = springDataProductoRepository;
        this.springDataCategoriaRepository = springDataCategoriaRepository;
        this.springDataMarcaRepository = springDataMarcaRepository;
        this.springDataProveedorRepository = springDataProveedorRepository;
    }

    // =========================================================================
    // ImplementaciÃ³n del Puerto de Salida
    // =========================================================================

    @Override
    public ProductoDomain guardar(ProductoDomain producto) {
        ProductoEntity entity       = toEntity(producto);
        
        // Cargar las relaciones antes de guardar para que el objeto persistido
        // las devuelva correctamente en la serializaciÃ³n hacia el frontend
        if (entity.getCategoriaId() != null) {
            springDataCategoriaRepository.findById(entity.getCategoriaId()).ifPresent(entity::setCategoria);
        }
        if (entity.getMarcaId() != null) {
            springDataMarcaRepository.findById(entity.getMarcaId()).ifPresent(entity::setMarca);
        }
        if (entity.getProveedorId() != null) {
            springDataProveedorRepository.findById(entity.getProveedorId()).ifPresent(entity::setProveedor);
        }

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
    // MÃ©todos de Mapeo Bidireccional (Domain <-> Entity)
    // =========================================================================

    /**
     * Convierte una entidad JPA en un modelo de dominio puro.
     * Se usa al leer datos desde la base de datos hacia la capa de aplicaciÃ³n.
     */
    private ProductoDomain toDomain(ProductoEntity entity) {
        if (entity == null) {
            return null;
        }
        ProductoDomain domain = new ProductoDomain(
                entity.getId(),
                entity.getCodigoBarras(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getPrecioCompra(),
                entity.getPrecioVenta(),
                entity.getCategoriaId(),
                entity.getMarcaId(),
                entity.getProveedorId(),
                entity.getStock(),
                entity.getEstado()
        );

        if (entity.getCategoria() != null) {
            domain.setCategoria(new CategoriaDomain(
                entity.getCategoria().getId(),
                entity.getCategoria().getNombre(),
                entity.getCategoria().getDescripcion()
            ));
        }
        if (entity.getMarca() != null) {
            domain.setMarca(new MarcaDomain(
                entity.getMarca().getId(),
                entity.getMarca().getNombre(),
                entity.getMarca().getDescripcion()
            ));
        }
        if (entity.getProveedor() != null) {
            domain.setProveedor(new ProveedorDomain(
                entity.getProveedor().getId(),
                entity.getProveedor().getNombre(),
                entity.getProveedor().getRucDni(),
                entity.getProveedor().getTelefono(),
                entity.getProveedor().getEmail(),
                entity.getProveedor().getDireccion()
            ));
        }

        return domain;
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
                domain.getPrecioCompra(),
                domain.getPrecioVenta(),
                domain.getCategoriaId(),
                domain.getMarcaId(),
                domain.getProveedorId(),
                domain.getStock(),
                domain.getEstado()
        );
    }
}

