package com.example.pos.repository;

import com.example.pos.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    /**
     * Lista todas las ventas cargando sus relaciones en UNA sola query (elimina N+1).
     * Nota: no se hace JOIN FETCH sobre detalles y pagos al mismo tiempo ya que
     * Hibernate no permite dos colecciones en FETCH simultáneo sin MultipleBagFetchException.
     * Se carga solo detalles (más relevante para la vista de historial).
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "LEFT JOIN FETCH v.sesionCaja " +
           "ORDER BY v.fecha DESC")
    List<Venta> findAllWithDetails();

    /**
     * Busca una venta por ID cargando sus relaciones y líneas de detalle.
     * Los pagos se inicializan en VentaService para evitar MultipleBagFetchException.
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "LEFT JOIN FETCH v.sesionCaja " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE v.id = :id")
    Optional<Venta> findByIdWithDetails(@Param("id") Integer id);

    /**
     * Lista ventas de una sesión de caja cargando relaciones esenciales.
     * Usado por CajaPage para mostrar las ventas del turno activo.
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "WHERE v.sesionCaja.id = :sesionCajaId " +
           "ORDER BY v.fecha DESC")
    List<Venta> findBySesionCajaIdWithDetails(@Param("sesionCajaId") Integer sesionCajaId);

    // Métodos simples (sin fetch) para búsquedas auxiliares
    List<Venta> findBySesionCajaId(Integer sesionCajaId);
    List<Venta> findByUsuarioId(Integer usuarioId);
    List<Venta> findBySesionCajaIdAndUsuarioId(Integer sesionCajaId, Integer usuarioId);
    List<Venta> findByClienteId(Integer clienteId);
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
