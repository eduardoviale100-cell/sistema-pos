package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.MarcaDomain;
import com.example.pos.domain.repository.MarcaRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia (Secondary/Driven Adapter) para el módulo de Marca.
 *
 * <p>Implementa el puerto de salida del dominio {@link MarcaRepositoryPort},
 * transformando datos entre el modelo de dominio puro y la entidad JPA.</p>
 */
@Repository
public class MarcaRepositoryAdapter implements MarcaRepositoryPort {

    private final SpringDataMarcaRepository springDataMarcaRepository;

    public MarcaRepositoryAdapter(SpringDataMarcaRepository springDataMarcaRepository) {
        this.springDataMarcaRepository = springDataMarcaRepository;
    }

    @Override
    public MarcaDomain guardar(MarcaDomain marca) {
        MarcaEntity entity = toEntity(marca);
        MarcaEntity guardada = springDataMarcaRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<MarcaDomain> buscarPorId(Long id) {
        return springDataMarcaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<MarcaDomain> buscarPorNombre(String nombre) {
        return springDataMarcaRepository.findByNombre(nombre)
                .map(this::toDomain);
    }

    @Override
    public List<MarcaDomain> listarTodas() {
        return springDataMarcaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void eliminarPorId(Long id) {
        springDataMarcaRepository.deleteById(id);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return springDataMarcaRepository.existsByNombre(nombre);
    }

    // =========================================================================
    // Mappings
    // =========================================================================

    private MarcaDomain toDomain(MarcaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MarcaDomain(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }

    private MarcaEntity toEntity(MarcaDomain domain) {
        if (domain == null) {
            return null;
        }
        return new MarcaEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getDescripcion()
        );
    }
}
