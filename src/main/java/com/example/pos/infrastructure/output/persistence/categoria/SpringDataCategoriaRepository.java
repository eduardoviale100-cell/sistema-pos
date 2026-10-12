package com.example.pos.infrastructure.output.persistence.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para CategoriaEntity.
 */
@Repository
public interface SpringDataCategoriaRepository extends JpaRepository<CategoriaEntity, Long> {

    Optional<CategoriaEntity> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}

