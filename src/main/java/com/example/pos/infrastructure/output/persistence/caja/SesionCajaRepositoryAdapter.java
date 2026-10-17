package com.example.pos.infrastructure.output.persistence.caja;

import com.example.pos.infrastructure.output.persistence.usuario.UsuarioRepositoryAdapter;
import com.example.pos.domain.model.caja.SesionCajaDomain;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia para SesionCaja.
 * Implementa SesionCajaRepositoryPort delegando a Spring Data JPA.
 */
@Repository
public class SesionCajaRepositoryAdapter implements SesionCajaRepositoryPort {

    private final SpringDataSesionCajaRepository springDataSesionCajaRepository;
    private final CajaRepositoryAdapter cajaRepositoryAdapter;
    private final UsuarioRepositoryAdapter usuarioRepositoryAdapter;

    public SesionCajaRepositoryAdapter(
            SpringDataSesionCajaRepository springDataSesionCajaRepository,
            CajaRepositoryAdapter cajaRepositoryAdapter,
            UsuarioRepositoryAdapter usuarioRepositoryAdapter) {
        this.springDataSesionCajaRepository = springDataSesionCajaRepository;
        this.cajaRepositoryAdapter = cajaRepositoryAdapter;
        this.usuarioRepositoryAdapter = usuarioRepositoryAdapter;
    }

    @Override
    public SesionCajaDomain guardar(SesionCajaDomain sesion) {
        SesionCajaEntity entity = toEntity(sesion);
        SesionCajaEntity guardada = springDataSesionCajaRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<SesionCajaDomain> buscarPorId(Long sesionId) {
        return springDataSesionCajaRepository.findById(sesionId).map(this::toDomain);
    }

    @Override
    public Optional<SesionCajaDomain> buscarSesionActivaPorCajaId(Long cajaId) {
        return springDataSesionCajaRepository
                .findFirstByCajaIdAndEstadoOrderByFechaAperturaDesc(cajaId, "ABIERTA")
                .map(this::toDomain);
    }

    @Override
    public List<SesionCajaDomain> listarPorCajaId(Long cajaId) {
        return springDataSesionCajaRepository.findByCajaId(cajaId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existeSesionAbierta(Long cajaId) {
        return springDataSesionCajaRepository.existsByCajaIdAndEstado(cajaId, "ABIERTA");
    }

    @Override
    public List<SesionCajaDomain> buscarSesionesAbiertasPorCajaId(Long cajaId) {
        return springDataSesionCajaRepository.findByCajaIdAndEstado(cajaId, "ABIERTA").stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<SesionCajaDomain> buscarSesionesAbiertasPorUsuarioId(Long usuarioId) {
        return springDataSesionCajaRepository.findByUsuarioIdAndEstado(usuarioId, "ABIERTA").stream()
                .map(this::toDomain)
                .toList();
    }

    // =========================================================================
    // Mappers Domain <-> Entity
    // =========================================================================

    public SesionCajaDomain toDomain(SesionCajaEntity entity) {
        if (entity == null) return null;
        return new SesionCajaDomain(
                entity.getId(),
                entity.getCaja() != null ? cajaRepositoryAdapter.toDomain(entity.getCaja()) : null,
                entity.getUsuario() != null ? usuarioRepositoryAdapter.toDomain(entity.getUsuario()) : null,
                entity.getFechaApertura(),
                entity.getMontoInicial(),
                entity.getFechaCierre(),
                entity.getMontoEsperado(),
                entity.getMontoReal(),
                entity.getDiferencia(),
                entity.getEstado()
        );
    }

    public SesionCajaEntity toEntity(SesionCajaDomain domain) {
        if (domain == null) return null;
        return SesionCajaEntity.builder()
                .id(domain.getId())
                .caja(domain.getCaja() != null ? cajaRepositoryAdapter.toEntity(domain.getCaja()) : null)
                .usuario(domain.getUsuario() != null ? usuarioRepositoryAdapter.toEntity(domain.getUsuario()) : null)
                .fechaApertura(domain.getFechaApertura())
                .montoInicial(domain.getMontoInicial())
                .fechaCierre(domain.getFechaCierre())
                .montoEsperado(domain.getMontoEsperado())
                .montoReal(domain.getMontoReal())
                .diferencia(domain.getDiferencia())
                .estado(domain.getEstado())
                .build();
    }
}