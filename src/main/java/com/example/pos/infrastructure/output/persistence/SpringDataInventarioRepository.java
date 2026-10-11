package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataInventarioRepository extends JpaRepository<InventarioEntity, Long> {

    @Query("SELECT i FROM InventarioEntity i LEFT JOIN FETCH i.producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.marca WHERE p.id = :productoId")
    Optional<InventarioEntity> findByProductoId(@Param("productoId") Long productoId);

    @Query("SELECT i FROM InventarioEntity i LEFT JOIN FETCH i.producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.marca ORDER BY i.id ASC")
    List<InventarioEntity> findAllWithProducto();

    @Query("SELECT i FROM InventarioEntity i LEFT JOIN FETCH i.producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.marca WHERE i.stockActual <= i.stockMinimo ORDER BY i.stockActual ASC")
    List<InventarioEntity> findBajoStock();
}
