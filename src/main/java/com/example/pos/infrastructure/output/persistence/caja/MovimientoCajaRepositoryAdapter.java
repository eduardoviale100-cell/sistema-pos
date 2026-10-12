package com.example.pos.infrastructure.output.persistence.caja;

import com.example.pos.infrastructure.output.persistence.usuario.UsuarioEntity;
import com.example.pos.infrastructure.output.persistence.usuario.UsuarioRepositoryAdapter;

import com.example.pos.domain.model.caja.*;
import com.example.pos.domain.model.caja.*;

import com.example.pos.domain.model.caja.MovimientoCajaDomain;
import com.example.pos.domain.repository.MovimientoCajaRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia para Movimientos de Caja.
 */
@Repository
public class MovimientoCajaRepositoryAdapter implements MovimientoCajaRepositoryPort {

    private final SpringDataMovimientoCajaRepository springDataMovimientoCajaRepository;
    private final SesionCajaRepositoryAdapter sesionCajaRepositoryAdapter;
    private final UsuarioRepositoryAdapter usuarioRepositoryAdapter;

    public MovimientoCajaRepositoryAdapter(
            SpringDataMovimientoCajaRepository springDataMovimientoCajaRepository,
            SesionCajaRepositoryAdapter sesionCajaRepositoryAdapter,
            UsuarioRepositoryAdapter usuarioRepositoryAdapter) {
        this.springDataMovimientoCajaRepository = springDataMovimientoCajaRepository;
        this.sesionCajaRepositoryAdapter = sesionCajaRepositoryAdapter;
        this.usuarioRepositoryAdapter = usuarioRepositoryAdapter;
    }

    @Override
    public MovimientoCajaDomain guardar(MovimientoCajaDomain movimiento) {
        MovimientoCajaEntity entity = toEntity(movimiento);
        MovimientoCajaEntity guardado = springDataMovimientoCajaRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public Optional<MovimientoCajaDomain> buscarPorId(Long id) {
        return springDataMovimientoCajaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<MovimientoCajaDomain> listarPorSesionCajaId(Long sesionCajaId) {
        return springDataMovimientoCajaRepository.findBySesionCajaId(sesionCajaId).stream()
                .map(this::toDomain)
                .toList();
    }

    // =========================================================================
    // Mappings
    // =========================================================================

    public MovimientoCajaDomain toDomain(MovimientoCajaEntity entity) {
        if (entity == null) return null;
        return new MovimientoCajaDomain(
                entity.getId(),
                entity.getSesionCaja() != null ? sesionCajaRepositoryAdapter.toDomain(entity.getSesionCaja()) : null,
                entity.getUsuario() != null ? usuarioRepositoryAdapter.toDomain(entity.getUsuario()) : null,
                entity.getTipoMovimiento(),
                entity.getMonto(),
                entity.getFecha(),
                entity.getReferencia(),
                entity.getObservaciones()
        );
    }

    public MovimientoCajaEntity toEntity(MovimientoCajaDomain domain) {
        if (domain == null) return null;
        return MovimientoCajaEntity.builder()
                .id(domain.getId())
                .sesionCaja(domain.getSesionCaja() != null ? sesionCajaRepositoryAdapter.toEntity(domain.getSesionCaja()) : null)
                .usuario(domain.getUsuario() != null ? usuarioRepositoryAdapter.toEntity(domain.getUsuario()) : null)
                .tipoMovimiento(domain.getTipoMovimiento())
                .monto(domain.getMonto())
                .fecha(domain.getFecha())
                .referencia(domain.getReferencia())
                .observaciones(domain.getObservaciones())
                .build();
    }
}
