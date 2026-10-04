package com.example.pos.infrastructure.output.persistence;

import com.example.pos.domain.model.RolUsuario;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de salida que implementa UsuarioRepositoryPort usando Spring Data JPA.
 */
@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository springDataRepo;

    public UsuarioRepositoryAdapter(SpringDataUsuarioRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    // ─── Mappers ───────────────────────────────────────────────────────────────

    private UsuarioDomain toDomain(UsuarioEntity entity) {
        return new UsuarioDomain(
                entity.getId(),
                entity.getNombre(),
                entity.getUsername(),
                entity.getPasswordHash(),
                RolUsuario.fromString(entity.getRol()),
                entity.getActivo(),
                entity.getFechaCreacion(),
                entity.getFechaModificacion()
        );
    }

    private UsuarioEntity toEntity(UsuarioDomain domain) {
        return UsuarioEntity.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .username(domain.getUsername())
                .passwordHash(domain.getPasswordHash())
                .rol(domain.getRol().name())
                .activo(domain.getActivo())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaModificacion(domain.getFechaModificacion())
                .build();
    }

    // ─── Puerto implementado ───────────────────────────────────────────────────

    @Override
    public UsuarioDomain guardar(UsuarioDomain usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity guardado = springDataRepo.save(entity);
        return toDomain(guardado);
    }

    @Override
    public Optional<UsuarioDomain> buscarPorId(Long id) {
        return springDataRepo.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<UsuarioDomain> buscarPorUsername(String username) {
        return springDataRepo.findByUsernameIgnoreCase(username).map(this::toDomain);
    }

    @Override
    public List<UsuarioDomain> listarTodos() {
        return springDataRepo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDomain> listarActivos() {
        return springDataRepo.findByActivoTrue().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDomain> listarPorRol(RolUsuario rol) {
        return springDataRepo.findByRol(rol.name()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorUsername(String username) {
        return springDataRepo.existsByUsernameIgnoreCase(username);
    }

    @Override
    public boolean existePorUsernameExcluyendoId(String username, Long idExcluido) {
        return springDataRepo.existsByUsernameIgnoreCaseAndIdNot(username, idExcluido);
    }

    @Override
    public void eliminarPorId(Long id) {
        springDataRepo.deleteById(id);
    }
}
