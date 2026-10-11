package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.CompraDetalleDomain;
import com.example.pos.domain.model.CompraDomain;
import com.example.pos.domain.repository.CompraRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia JPA para Compras (implementa CompraRepositoryPort).
 */
@Repository
public class CompraRepositoryAdapter implements CompraRepositoryPort {

    private final SpringDataCompraRepository springDataCompraRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CompraRepositoryAdapter(SpringDataCompraRepository springDataCompraRepository) {
        this.springDataCompraRepository = springDataCompraRepository;
    }

    @Override
    public CompraDomain guardar(CompraDomain compra) {
        CompraEntity entity = toEntity(compra);
        CompraEntity guardada = springDataCompraRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<CompraDomain> buscarPorId(Long id) {
        return springDataCompraRepository.findByIdWithDetails(id).map(this::toDomain);
    }

    @Override
    public List<CompraDomain> listarTodas() {
        return springDataCompraRepository.findAllWithDetails().stream()
                .map(this::toDomain)
                .toList();
    }

    // =========================================================================
    // Mapeos Entidad <-> Dominio
    // =========================================================================

    public CompraDomain toDomain(CompraEntity entity) {
        if (entity == null) return null;

        CompraDomain domain = new CompraDomain();
        domain.setId(entity.getId());
        if (entity.getProveedor() != null) {
            domain.setProveedorId(entity.getProveedor().getId());
            domain.setProveedorNombre(entity.getProveedor().getNombre());
        }
        if (entity.getUsuario() != null) {
            domain.setUsuarioId(entity.getUsuario().getId());
            domain.setUsuarioNombre(entity.getUsuario().getNombre());
        }
        domain.setFecha(entity.getFecha());
        domain.setTotal(entity.getTotal());
        domain.setObservaciones(entity.getObservaciones());

        if (entity.getDetalles() != null) {
            List<CompraDetalleDomain> detalles = entity.getDetalles().stream().map(d -> {
                CompraDetalleDomain item = new CompraDetalleDomain();
                item.setId(d.getId());
                if (d.getProducto() != null) {
                    item.setProductoId(d.getProducto().getId());
                    item.setProductoNombre(d.getProducto().getNombre());
                }
                item.setCantidad(d.getCantidad());
                item.setPrecioUnitario(d.getPrecioUnitario());
                item.setSubtotal(d.getSubtotal() != null ? d.getSubtotal() : item.calcularSubtotal());
                return item;
            }).toList();
            domain.setDetalles(detalles);
        }

        return domain;
    }

    public CompraEntity toEntity(CompraDomain domain) {
        if (domain == null) return null;

        CompraEntity entity = new CompraEntity();
        entity.setId(domain.getId());
        entity.setFecha(domain.getFecha() != null ? domain.getFecha() : LocalDateTime.now());
        entity.setTotal(domain.getTotal());
        entity.setObservaciones(domain.getObservaciones());

        if (domain.getProveedorId() != null) {
            entity.setProveedor(entityManager.getReference(ProveedorEntity.class, domain.getProveedorId()));
        }
        if (domain.getUsuarioId() != null) {
            entity.setUsuario(entityManager.getReference(UsuarioEntity.class, domain.getUsuarioId()));
        }

        if (domain.getDetalles() != null) {
            List<CompraDetalleEntity> listDetalles = new ArrayList<>();
            for (CompraDetalleDomain d : domain.getDetalles()) {
                CompraDetalleEntity de = new CompraDetalleEntity();
                de.setId(d.getId());
                de.setCompra(entity);
                if (d.getProductoId() != null) {
                    de.setProducto(entityManager.getReference(ProductoEntity.class, d.getProductoId()));
                }
                de.setCantidad(d.getCantidad());
                de.setPrecioUnitario(d.getPrecioUnitario());
                listDetalles.add(de);
            }
            entity.setDetalles(listDetalles);
        }

        return entity;
    }
}
