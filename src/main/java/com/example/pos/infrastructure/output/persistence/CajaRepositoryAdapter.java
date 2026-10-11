package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.CajaDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia para Caja (implementa CajaRepositoryPort).
 */
@Repository
public class CajaRepositoryAdapter implements CajaRepositoryPort {

    private final SpringDataCajaRepository springDataCajaRepository;

    public CajaRepositoryAdapter(SpringDataCajaRepository springDataCajaRepository) {
        this.springDataCajaRepository = springDataCajaRepository;
    }

    @Override
    public CajaDomain guardar(CajaDomain domain) {
        CajaEntity entity = toEntity(domain);
        CajaEntity guardada = springDataCajaRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<CajaDomain> buscarPorId(Long id) {
        return springDataCajaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<CajaDomain> buscarPorNombre(String nombre) {
        return springDataCajaRepository.findByNombre(nombre).map(this::toDomain);
    }

    @Override
    public List<CajaDomain> listarTodas() {
        return springDataCajaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void eliminarPorId(Long id) {
        springDataCajaRepository.deleteById(id);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return springDataCajaRepository.existsByNombre(nombre);
    }

    @Override
    public boolean existePorNombreExcluyendoId(String nombre, Long idExcluido) {
        return springDataCajaRepository.existsByNombreAndIdNot(nombre, idExcluido);
    }

    // =========================================================================
    // Mappings
    // =========================================================================

    public CajaDomain toDomain(CajaEntity entity) {
        if (entity == null) return null;
        return new CajaDomain(
                entity.getId(),
                entity.getNombre(),
                entity.getEstado()
        );
    }

    public CajaEntity toEntity(CajaDomain domain) {
        if (domain == null) return null;
        return CajaEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .estado(domain.getEstado())
                .build();
    }
}
