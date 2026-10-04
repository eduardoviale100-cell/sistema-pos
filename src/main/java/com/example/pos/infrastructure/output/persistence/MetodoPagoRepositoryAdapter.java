package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.MetodoPagoDomain;
import com.example.pos.domain.repository.MetodoPagoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de salida que implementa MetodoPagoRepositoryPort usando Spring Data JPA.
 */
@Component
public class MetodoPagoRepositoryAdapter implements MetodoPagoRepositoryPort {

    private final SpringDataMetodoPagoRepository springDataRepo;

    public MetodoPagoRepositoryAdapter(SpringDataMetodoPagoRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    // ─── Mappers ───────────────────────────────────────────────────────────────

    private MetodoPagoDomain toDomain(MetodoPagoEntity entity) {
        return new MetodoPagoDomain(entity.getId(), entity.getNombre(), entity.getActivo());
    }

    private MetodoPagoEntity toEntity(MetodoPagoDomain domain) {
        return MetodoPagoEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .activo(domain.getActivo())
                .build();
    }

    // ─── Puerto implementado ───────────────────────────────────────────────────

    @Override
    public MetodoPagoDomain guardar(MetodoPagoDomain metodoPago) {
        MetodoPagoEntity entity = toEntity(metodoPago);
        return toDomain(springDataRepo.save(entity));
    }

    @Override
    public Optional<MetodoPagoDomain> buscarPorId(Long id) {
        return springDataRepo.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<MetodoPagoDomain> buscarPorNombre(String nombre) {
        return springDataRepo.findByNombreIgnoreCase(nombre).map(this::toDomain);
    }

    @Override
    public List<MetodoPagoDomain> listarTodos() {
        return springDataRepo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MetodoPagoDomain> listarActivos() {
        return springDataRepo.findByActivoTrue().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarPorId(Long id) {
        springDataRepo.deleteById(id);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return springDataRepo.findByNombreIgnoreCase(nombre).isPresent();
    }
}
