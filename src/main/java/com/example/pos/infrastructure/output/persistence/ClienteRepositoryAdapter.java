package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.ClienteDomain;
import com.example.pos.domain.repository.ClienteRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de Persistencia (Secondary/Driven Adapter) para el módulo de Cliente.
 * Implementa el puerto de salida del dominio (ClienteRepositoryPort) interactuando
 * con Spring Data JPA y transformando datos entre el modelo de dominio y la entidad de base de datos.
 */
@Repository
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final SpringDataClienteRepository springDataClienteRepository;

    public ClienteRepositoryAdapter(SpringDataClienteRepository springDataClienteRepository) {
        this.springDataClienteRepository = springDataClienteRepository;
    }

    @Override
    public Optional<ClienteDomain> buscarPorId(Long id) {
        return springDataClienteRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<ClienteDomain> buscarPorRucDni(String rucDni) {
        return springDataClienteRepository.findByRucDni(rucDni)
                .map(this::toDomain);
    }

    @Override
    public ClienteDomain guardar(ClienteDomain cliente) {
        ClienteEntity entity = toEntity(cliente);
        ClienteEntity entityGuardada = springDataClienteRepository.save(entity);
        return toDomain(entityGuardada);
    }

    @Override
    public List<ClienteDomain> listarTodos() {
        return springDataClienteRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        springDataClienteRepository.deleteById(id);
    }

    @Override
    public boolean existePorRucDni(String rucDni) {
        return springDataClienteRepository.existsByRucDni(rucDni);
    }

    // --- Métodos de Mapeo (Domain <-> Entity) ---

    private ClienteDomain toDomain(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ClienteDomain(
                entity.getId(),
                entity.getNombre(),
                entity.getRucDni(),
                entity.getTelefono(),
                entity.getEmail(),
                entity.getDireccion(),
                entity.getEstado()
        );
    }

    private ClienteEntity toEntity(ClienteDomain domain) {
        if (domain == null) {
            return null;
        }
        return new ClienteEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getRucDni(),
                domain.getTelefono(),
                domain.getEmail(),
                domain.getDireccion(),
                domain.getEstado()
        );
    }
}
