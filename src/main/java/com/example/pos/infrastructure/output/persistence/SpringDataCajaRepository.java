package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCajaRepository extends JpaRepository<CajaEntity, Long> {
    Optional<CajaEntity> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    boolean existsByNombreAndIdNot(String nombre, Long id);
}
