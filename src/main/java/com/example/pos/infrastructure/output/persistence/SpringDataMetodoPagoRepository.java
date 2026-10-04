package com.example.pos.infrastructure.output.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para MetodoPagoEntity.
 */
public interface SpringDataMetodoPagoRepository extends JpaRepository<MetodoPagoEntity, Long> {

    java.util.Optional<MetodoPagoEntity> findByNombreIgnoreCase(String nombre);

    List<MetodoPagoEntity> findByActivoTrue();
}
