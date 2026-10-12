package com.example.pos.infrastructure.output.persistence.inventario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMovimientoInventarioRepository extends JpaRepository<MovimientoInventarioEntity, Long> {

    @Query("SELECT m FROM MovimientoInventarioEntity m " +
           "LEFT JOIN FETCH m.producto " +
           "LEFT JOIN FETCH m.usuario " +
           "WHERE m.producto.id = :productoId " +
           "ORDER BY m.fecha DESC")
    List<MovimientoInventarioEntity> findByProductoId(@Param("productoId") Long productoId);

    @Query("SELECT m FROM MovimientoInventarioEntity m " +
           "LEFT JOIN FETCH m.producto " +
           "LEFT JOIN FETCH m.usuario " +
           "ORDER BY m.fecha DESC")
    List<MovimientoInventarioEntity> findAllWithDetails();
}

