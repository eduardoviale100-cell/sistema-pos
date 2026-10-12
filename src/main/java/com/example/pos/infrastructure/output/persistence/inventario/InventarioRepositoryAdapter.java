package com.example.pos.infrastructure.output.persistence.inventario;


import com.example.pos.infrastructure.output.persistence.producto.ProductoEntity;
import com.example.pos.infrastructure.output.persistence.usuario.UsuarioEntity;
import com.example.pos.domain.model.marca.MarcaDomain;
import com.example.pos.domain.model.producto.ProductoDomain;
import com.example.pos.domain.model.categoria.CategoriaDomain;
import com.example.pos.domain.model.inventario.InventarioDomain;
import com.example.pos.domain.model.inventario.MovimientoInventarioDomain;
import com.example.pos.domain.repository.InventarioRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia JPA para Inventario y Kardex (implementa InventarioRepositoryPort).
 */
@Repository
public class InventarioRepositoryAdapter implements InventarioRepositoryPort {

    private final SpringDataInventarioRepository springDataInventarioRepository;
    private final SpringDataMovimientoInventarioRepository springDataMovimientoInventarioRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public InventarioRepositoryAdapter(
            SpringDataInventarioRepository springDataInventarioRepository,
            SpringDataMovimientoInventarioRepository springDataMovimientoInventarioRepository) {
        this.springDataInventarioRepository = springDataInventarioRepository;
        this.springDataMovimientoInventarioRepository = springDataMovimientoInventarioRepository;
    }

    @Override
    public InventarioDomain guardar(InventarioDomain inventario) {
        InventarioEntity entity = toEntity(inventario);
        InventarioEntity guardado = springDataInventarioRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public Optional<InventarioDomain> buscarPorId(Long id) {
        return springDataInventarioRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<InventarioDomain> buscarPorProductoId(Long productoId) {
        return springDataInventarioRepository.findByProductoId(productoId).map(this::toDomain);
    }

    @Override
    public List<InventarioDomain> listarTodos() {
        return springDataInventarioRepository.findAllWithProducto().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<InventarioDomain> listarBajoStock() {
        return springDataInventarioRepository.findBajoStock().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public MovimientoInventarioDomain registrarMovimiento(MovimientoInventarioDomain movimiento) {
        MovimientoInventarioEntity entity = toEntity(movimiento);
        MovimientoInventarioEntity guardado = springDataMovimientoInventarioRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public List<MovimientoInventarioDomain> listarMovimientosPorProductoId(Long productoId) {
        return springDataMovimientoInventarioRepository.findByProductoId(productoId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<MovimientoInventarioDomain> listarTodosLosMovimientos() {
        return springDataMovimientoInventarioRepository.findAllWithDetails().stream()
                .map(this::toDomain)
                .toList();
    }

    // =========================================================================
    // Mappings Entidad <-> Dominio
    // =========================================================================

    public InventarioDomain toDomain(InventarioEntity entity) {
        if (entity == null) return null;

        InventarioDomain domain = new InventarioDomain();
        domain.setId(entity.getId());
        if (entity.getProducto() != null) {
            domain.setProductoId(entity.getProducto().getId());
            domain.setProductoNombre(entity.getProducto().getNombre());
            domain.setProductoCodigo(entity.getProducto().getCodigoBarras());

            // Map the nested product for frontend serialization
            com.example.pos.domain.model.producto.ProductoDomain productoDomain = new com.example.pos.domain.model.producto.ProductoDomain();
            productoDomain.setId(entity.getProducto().getId());
            productoDomain.setNombre(entity.getProducto().getNombre());
            productoDomain.setCodigoBarras(entity.getProducto().getCodigoBarras());
            
            if (entity.getProducto().getCategoria() != null) {
                productoDomain.setCategoria(new com.example.pos.domain.model.categoria.CategoriaDomain(
                    entity.getProducto().getCategoria().getId(),
                    entity.getProducto().getCategoria().getNombre(),
                    entity.getProducto().getCategoria().getDescripcion()
                ));
            }
            if (entity.getProducto().getMarca() != null) {
                productoDomain.setMarca(new com.example.pos.domain.model.marca.MarcaDomain(
                    entity.getProducto().getMarca().getId(),
                    entity.getProducto().getMarca().getNombre(),
                    entity.getProducto().getMarca().getDescripcion()
                ));
            }
            domain.setProducto(productoDomain);
        }
        domain.setStockActual(entity.getStockActual());
        domain.setStockMinimo(entity.getStockMinimo());
        domain.setUltimaActualizacion(entity.getUltimaActualizacion());
        return domain;
    }

    public InventarioEntity toEntity(InventarioDomain domain) {
        if (domain == null) return null;

        InventarioEntity entity = new InventarioEntity();
        entity.setId(domain.getId());
        if (domain.getProductoId() != null) {
            entity.setProducto(entityManager.getReference(ProductoEntity.class, domain.getProductoId()));
        }
        entity.setStockActual(domain.getStockActual());
        entity.setStockMinimo(domain.getStockMinimo());
        entity.setUltimaActualizacion(domain.getUltimaActualizacion() != null ? domain.getUltimaActualizacion() : LocalDateTime.now());
        return entity;
    }

    public MovimientoInventarioDomain toDomain(MovimientoInventarioEntity entity) {
        if (entity == null) return null;

        MovimientoInventarioDomain domain = new MovimientoInventarioDomain();
        domain.setId(entity.getId());
        if (entity.getProducto() != null) {
            domain.setProductoId(entity.getProducto().getId());
            domain.setProductoNombre(entity.getProducto().getNombre());
        }
        domain.setTipoMovimiento(entity.getTipoMovimiento());
        domain.setCantidad(entity.getCantidad());
        domain.setFecha(entity.getFecha());
        if (entity.getUsuario() != null) {
            domain.setUsuarioId(entity.getUsuario().getId());
            domain.setUsuarioNombre(entity.getUsuario().getNombre());
        }
        domain.setReferencia(entity.getReferencia());
        domain.setObservaciones(entity.getObservaciones());
        return domain;
    }

    public MovimientoInventarioEntity toEntity(MovimientoInventarioDomain domain) {
        if (domain == null) return null;

        MovimientoInventarioEntity entity = new MovimientoInventarioEntity();
        entity.setId(domain.getId());
        if (domain.getProductoId() != null) {
            entity.setProducto(entityManager.getReference(ProductoEntity.class, domain.getProductoId()));
        }
        entity.setTipoMovimiento(domain.getTipoMovimiento());
        entity.setCantidad(domain.getCantidad());
        entity.setFecha(domain.getFecha() != null ? domain.getFecha() : LocalDateTime.now());
        if (domain.getUsuarioId() != null) {
            entity.setUsuario(entityManager.getReference(UsuarioEntity.class, domain.getUsuarioId()));
        }
        entity.setReferencia(domain.getReferencia());
        entity.setObservaciones(domain.getObservaciones());
        return entity;
    }
}

