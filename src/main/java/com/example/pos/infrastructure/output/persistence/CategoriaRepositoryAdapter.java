package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.CategoriaDomain;
import com.example.pos.domain.repository.CategoriaRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia (Secondary/Driven Adapter) para el módulo de Categoría.
 *
 * <p>Implementa el puerto de salida del dominio {@link CategoriaRepositoryPort},
 * transformando datos entre el modelo de dominio puro y la entidad JPA.</p>
 */
@Repository
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final SpringDataCategoriaRepository springDataCategoriaRepository;

    public CategoriaRepositoryAdapter(SpringDataCategoriaRepository springDataCategoriaRepository) {
        this.springDataCategoriaRepository = springDataCategoriaRepository;
    }

    @Override
    public CategoriaDomain guardar(CategoriaDomain categoria) {
        CategoriaEntity entity = toEntity(categoria);
        CategoriaEntity guardada = springDataCategoriaRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<CategoriaDomain> buscarPorId(Long id) {
        return springDataCategoriaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<CategoriaDomain> buscarPorNombre(String nombre) {
        return springDataCategoriaRepository.findByNombre(nombre)
                .map(this::toDomain);
    }

    @Override
    public List<CategoriaDomain> listarTodas() {
        return springDataCategoriaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void eliminarPorId(Long id) {
        springDataCategoriaRepository.deleteById(id);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return springDataCategoriaRepository.existsByNombre(nombre);
    }

    // =========================================================================
    // Mappings
    // =========================================================================

    private CategoriaDomain toDomain(CategoriaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CategoriaDomain(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }

    private CategoriaEntity toEntity(CategoriaDomain domain) {
        if (domain == null) {
            return null;
        }
        return new CategoriaEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getDescripcion()
        );
    }
}
