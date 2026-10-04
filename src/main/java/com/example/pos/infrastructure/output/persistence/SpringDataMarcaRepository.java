package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para MarcaEntity.
 */
@Repository
public interface SpringDataMarcaRepository extends JpaRepository<MarcaEntity, Long> {

    Optional<MarcaEntity> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
