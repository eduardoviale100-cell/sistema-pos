package com.example.pos.infrastructure.output.persistence.compra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataCompraRepository extends JpaRepository<CompraEntity, Long> {

    @Query("SELECT DISTINCT c FROM CompraEntity c " +
           "LEFT JOIN FETCH c.proveedor " +
           "LEFT JOIN FETCH c.usuario " +
           "LEFT JOIN FETCH c.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "ORDER BY c.fecha DESC")
    List<CompraEntity> findAllWithDetails();

    @Query("SELECT DISTINCT c FROM CompraEntity c " +
           "LEFT JOIN FETCH c.proveedor " +
           "LEFT JOIN FETCH c.usuario " +
           "LEFT JOIN FETCH c.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE c.id = :id")
    Optional<CompraEntity> findByIdWithDetails(@Param("id") Long id);
}

