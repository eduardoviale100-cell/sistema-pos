package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para ProveedorEntity.
 */
@Repository
public interface SpringDataProveedorRepository extends JpaRepository<ProveedorEntity, Long> {

    Optional<ProveedorEntity> findByRucDni(String rucDni);

    boolean existsByRucDni(String rucDni);
}
