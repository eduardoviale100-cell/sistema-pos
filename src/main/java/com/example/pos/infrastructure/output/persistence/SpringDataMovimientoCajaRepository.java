package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMovimientoCajaRepository extends JpaRepository<MovimientoCajaEntity, Long> {
    List<MovimientoCajaEntity> findBySesionCajaId(Long sesionCajaId);
}
