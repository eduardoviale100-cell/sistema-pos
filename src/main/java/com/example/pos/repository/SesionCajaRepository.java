package com.example.pos.repository;

import com.example.pos.model.SesionCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SesionCajaRepository extends JpaRepository<SesionCaja, Integer> {
    Optional<SesionCaja> findByCajaIdAndEstado(Integer cajaId, String estado);
    List<SesionCaja> findByEstado(String estado);
    List<SesionCaja> findByUsuarioId(Integer usuarioId);
}
