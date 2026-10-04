package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.ProveedorDomain;
import com.example.pos.domain.repository.ProveedorRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia (Secondary/Driven Adapter) para el módulo Proveedor.
 * Implementa el puerto de salida del dominio {@link ProveedorRepositoryPort}.
 */
@Repository
public class ProveedorRepositoryAdapter implements ProveedorRepositoryPort {

    private final SpringDataProveedorRepository springDataProveedorRepository;

    public ProveedorRepositoryAdapter(SpringDataProveedorRepository springDataProveedorRepository) {
        this.springDataProveedorRepository = springDataProveedorRepository;
    }

    @Override
    public ProveedorDomain guardar(ProveedorDomain proveedor) {
        ProveedorEntity entity = toEntity(proveedor);
        ProveedorEntity guardado = springDataProveedorRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public Optional<ProveedorDomain> buscarPorId(Long id) {
        return springDataProveedorRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<ProveedorDomain> buscarPorRucDni(String rucDni) {
        return springDataProveedorRepository.findByRucDni(rucDni)
                .map(this::toDomain);
    }

    @Override
    public List<ProveedorDomain> listarTodos() {
        return springDataProveedorRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void eliminarPorId(Long id) {
        springDataProveedorRepository.deleteById(id);
    }

    @Override
    public boolean existePorRucDni(String rucDni) {
        return springDataProveedorRepository.existsByRucDni(rucDni);
    }

    // =========================================================================
    // Mapeos (Domain <-> Entity)
    // =========================================================================

    private ProveedorDomain toDomain(ProveedorEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ProveedorDomain(
                entity.getId(),
                entity.getNombre(),
                entity.getRucDni(),
                entity.getTelefono(),
                entity.getEmail(),
                entity.getDireccion()
        );
    }

    private ProveedorEntity toEntity(ProveedorDomain domain) {
        if (domain == null) {
            return null;
        }
        return new ProveedorEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getRucDni(),
                domain.getTelefono(),
                domain.getEmail(),
                domain.getDireccion()
        );
    }
}
