package com.example.pos.infrastructure.output.persistence.caja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para SesionCajaEntity.
 * Solo define las queries derivadas necesarias para el puerto SesionCajaRepositoryPort.
 */
@Repository
public interface SpringDataSesionCajaRepository extends JpaRepository<SesionCajaEntity, Long> {

    /** Busca la sesion mas reciente ABIERTA para una caja (para obtener sesion activa). */
    Optional<SesionCajaEntity> findFirstByCajaIdAndEstadoOrderByFechaAperturaDesc(Long cajaId, String estado);

    /** Busca todas las sesiones con un estado dado para una caja (para cierre de huerfanas). */
    List<SesionCajaEntity> findByCajaIdAndEstado(Long cajaId, String estado);

    /** Busca todas las sesiones con un estado dado para un usuario (tolerancia multi-caja). */
    List<SesionCajaEntity> findByUsuarioIdAndEstado(Long usuarioId, String estado);

    /** Lista el historial de sesiones de una caja sin filtro de estado. */
    List<SesionCajaEntity> findByCajaId(Long cajaId);

    /** Verifica existencia de sesion ABIERTA para una caja (check rapido). */
    boolean existsByCajaIdAndEstado(Long cajaId, String estado);
}