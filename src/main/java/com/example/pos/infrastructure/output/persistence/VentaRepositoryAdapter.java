package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.PagoDomain;
import com.example.pos.domain.model.VentaDetalleDomain;
import com.example.pos.domain.model.VentaDomain;
import com.example.pos.domain.repository.VentaRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Adaptador de Persistencia JPA para el Agregado Venta (implementa VentaRepositoryPort).
 */
@Repository
public class VentaRepositoryAdapter implements VentaRepositoryPort {

    private final SpringDataVentaRepository springDataVentaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public VentaRepositoryAdapter(SpringDataVentaRepository springDataVentaRepository) {
        this.springDataVentaRepository = springDataVentaRepository;
    }

    @Override
    public VentaDomain guardar(VentaDomain venta) {
        VentaEntity entity = toEntity(venta);
        VentaEntity guardada = springDataVentaRepository.save(entity);
        if (entityManager != null) {
            entityManager.flush();
        }
        return springDataVentaRepository.findByIdWithDetails(guardada.getId())
                .map(this::toDomain)
                .orElseGet(() -> toDomain(guardada));
    }

    @Override
    public Optional<VentaDomain> buscarPorId(Long id) {
        return springDataVentaRepository.findByIdWithDetails(id).map(this::toDomain);
    }

    @Override
    public Optional<VentaDomain> buscarPorCodigoFactura(String codigoFactura) {
        return springDataVentaRepository.findByCodigoFacturaWithDetails(codigoFactura).map(this::toDomain);
    }

    @Override
    public List<VentaDomain> listarTodas() {
        return springDataVentaRepository.findAllWithDetails().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<VentaDomain> listarPorSesionCajaId(Long sesionCajaId) {
        return springDataVentaRepository.findBySesionCajaIdWithDetails(sesionCajaId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<VentaDomain> listarPorUsuarioId(Long usuarioId) {
        return springDataVentaRepository.findByUsuarioIdWithDetails(usuarioId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<VentaDomain> listarPorSesionCajaIdYUsuarioId(Long sesionCajaId, Long usuarioId) {
        return springDataVentaRepository.findBySesionCajaIdAndUsuarioIdWithDetails(sesionCajaId, usuarioId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long contarTotalVentas() {
        return springDataVentaRepository.count();
    }

    // =========================================================================
    // Mapeos Entidad <-> Dominio
    // =========================================================================

    public VentaDomain toDomain(VentaEntity entity) {
        if (entity == null) {
            return null;
        }

        VentaDomain domain = new VentaDomain();
        domain.setId(entity.getId());
        if (entity.getCliente() != null) {
            domain.setClienteId(entity.getCliente().getId());
            domain.setClienteNombre(entity.getCliente().getNombre());
            domain.setClienteDocumento(entity.getCliente().getRucDni());
        }
        if (entity.getUsuario() != null) {
            domain.setUsuarioId(entity.getUsuario().getId());
            domain.setUsuarioNombre(entity.getUsuario().getNombre());
        }
        if (entity.getSesionCaja() != null) {
            domain.setSesionCajaId(entity.getSesionCaja().getId());
        }
        domain.setCodigoFactura(entity.getCodigoFactura());
        domain.setFecha(entity.getFecha());
        domain.setTotal(entity.getTotal());
        domain.setObservaciones(entity.getObservaciones());

        if (entity.getObservaciones() != null && entity.getObservaciones().toUpperCase().contains("ANULADA")) {
            domain.setEstado("ANULADA");
        } else {
            domain.setEstado("COMPLETADA");
        }

        if (entity.getDetalles() != null) {
            List<VentaDetalleDomain> detalles = entity.getDetalles().stream().map(d -> {
                VentaDetalleDomain item = new VentaDetalleDomain();
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

        if (entity.getPagos() != null) {
            List<PagoDomain> pagos = entity.getPagos().stream().map(p -> {
                PagoDomain pago = new PagoDomain();
                pago.setId(p.getId());
                if (p.getMetodoPago() != null) {
                    pago.setMetodoPagoId(p.getMetodoPago().getId());
                    pago.setMetodoPagoNombre(p.getMetodoPago().getNombre());
                }
                pago.setMonto(p.getMonto());
                pago.setFecha(p.getFecha());
                return pago;
            }).toList();
            domain.setPagos(pagos);
        }

        return domain;
    }

    public VentaEntity toEntity(VentaDomain domain) {
        if (domain == null) {
            return null;
        }

        VentaEntity entity = new VentaEntity();
        entity.setId(domain.getId());
        entity.setCodigoFactura(domain.getCodigoFactura());
        entity.setFecha(domain.getFecha() != null ? domain.getFecha() : LocalDateTime.now());
        entity.setTotal(domain.getTotal());
        String observaciones = domain.getObservaciones();
        if (domain.isAnulada() && (observaciones == null || !observaciones.toUpperCase().contains("ANULADA"))) {
            observaciones = (observaciones != null ? observaciones + " | " : "") + "ANULADA";
        }
        entity.setObservaciones(observaciones);

        if (domain.getClienteId() != null) {
            entity.setCliente(entityManager.getReference(ClienteEntity.class, domain.getClienteId()));
        }
        if (domain.getUsuarioId() != null) {
            entity.setUsuario(entityManager.getReference(UsuarioEntity.class, domain.getUsuarioId()));
        }
        if (domain.getSesionCajaId() != null) {
            entity.setSesionCaja(entityManager.getReference(SesionCajaEntity.class, domain.getSesionCajaId()));
        }

        if (domain.getDetalles() != null) {
            Set<VentaDetalleEntity> setDetalles = new LinkedHashSet<>();
            for (VentaDetalleDomain d : domain.getDetalles()) {
                VentaDetalleEntity de = new VentaDetalleEntity();
                de.setId(d.getId());
                de.setVenta(entity);
                if (d.getProductoId() != null) {
                    de.setProducto(entityManager.getReference(ProductoEntity.class, d.getProductoId()));
                }
                de.setCantidad(d.getCantidad());
                de.setPrecioUnitario(d.getPrecioUnitario());
                setDetalles.add(de);
            }
            entity.setDetalles(setDetalles);
        }

        if (domain.getPagos() != null) {
            Set<PagoEntity> setPagos = new LinkedHashSet<>();
            for (PagoDomain p : domain.getPagos()) {
                PagoEntity pe = new PagoEntity();
                pe.setId(p.getId());
                pe.setVenta(entity);
                if (p.getMetodoPagoId() != null) {
                    pe.setMetodoPago(entityManager.getReference(MetodoPagoEntity.class, p.getMetodoPagoId()));
                }
                pe.setMonto(p.getMonto());
                pe.setFecha(p.getFecha() != null ? p.getFecha() : LocalDateTime.now());
                setPagos.add(pe);
            }
            entity.setPagos(setPagos);
        }

        return entity;
    }
}
