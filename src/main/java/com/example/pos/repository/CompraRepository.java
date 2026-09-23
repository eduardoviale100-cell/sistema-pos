package com.example.pos.repository;

import com.example.pos.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

    /**
     * Carga todas las compras con sus relaciones en UNA sola query SQL (JOIN FETCH).
     * Esto elimina el problema N+1 que causaba timeouts contra la BD remota Neon:
     * sin esto, Hibernate hacía una query extra por cada proveedor, usuario y producto
     * de cada detalle.
     * DISTINCT evita duplicados generados por el JOIN con la colección de detalles.
     */
    @Query("SELECT DISTINCT c FROM Compra c " +
           "LEFT JOIN FETCH c.proveedor " +
           "LEFT JOIN FETCH c.usuario " +
           "LEFT JOIN FETCH c.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "ORDER BY c.fecha DESC")
    List<Compra> findAllWithDetails();

    /**
     * Carga una compra por ID con todas sus relaciones (para el endpoint GET /{id}).
     */
    @Query("SELECT c FROM Compra c " +
           "LEFT JOIN FETCH c.proveedor " +
           "LEFT JOIN FETCH c.usuario " +
           "LEFT JOIN FETCH c.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE c.id = :id")
    Optional<Compra> findByIdWithDetails(Integer id);

    List<Compra> findByProveedorId(Integer proveedorId);
    List<Compra> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
