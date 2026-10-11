package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataSesionCajaRepository extends JpaRepository<SesionCajaEntity, Long> {
    Optional<SesionCajaEntity> findByCajaIdAndEstado(Long cajaId, String estado);
    List<SesionCajaEntity> findByCajaId(Long cajaId);
    boolean existsByCajaIdAndEstado(Long cajaId, String estado);
}
