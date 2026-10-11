package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataVentaRepository extends JpaRepository<VentaEntity, Long> {

    @Query("SELECT DISTINCT v FROM VentaEntity v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "LEFT JOIN FETCH v.sesionCaja " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH v.pagos p " +
           "LEFT JOIN FETCH p.metodoPago " +
           "ORDER BY v.fecha DESC")
    List<VentaEntity> findAllWithDetails();

    @Query("SELECT DISTINCT v FROM VentaEntity v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "LEFT JOIN FETCH v.sesionCaja " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH v.pagos p " +
           "LEFT JOIN FETCH p.metodoPago " +
           "WHERE v.id = :id")
    Optional<VentaEntity> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT v FROM VentaEntity v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "LEFT JOIN FETCH v.sesionCaja " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH v.pagos p " +
           "LEFT JOIN FETCH p.metodoPago " +
           "WHERE v.codigoFactura = :codigoFactura")
    Optional<VentaEntity> findByCodigoFacturaWithDetails(@Param("codigoFactura") String codigoFactura);

    @Query("SELECT DISTINCT v FROM VentaEntity v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "LEFT JOIN FETCH v.sesionCaja " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH v.pagos p " +
           "LEFT JOIN FETCH p.metodoPago " +
           "WHERE v.sesionCaja.id = :sesionCajaId " +
           "ORDER BY v.fecha DESC")
    List<VentaEntity> findBySesionCajaIdWithDetails(@Param("sesionCajaId") Long sesionCajaId);

    @Query("SELECT DISTINCT v FROM VentaEntity v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "LEFT JOIN FETCH v.sesionCaja " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH v.pagos p " +
           "LEFT JOIN FETCH p.metodoPago " +
           "WHERE v.usuario.id = :usuarioId " +
           "ORDER BY v.fecha DESC")
    List<VentaEntity> findByUsuarioIdWithDetails(@Param("usuarioId") Long usuarioId);

    @Query("SELECT DISTINCT v FROM VentaEntity v " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.usuario " +
           "LEFT JOIN FETCH v.sesionCaja " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH v.pagos p " +
           "LEFT JOIN FETCH p.metodoPago " +
           "WHERE v.sesionCaja.id = :sesionCajaId AND v.usuario.id = :usuarioId " +
           "ORDER BY v.fecha DESC")
    List<VentaEntity> findBySesionCajaIdAndUsuarioIdWithDetails(@Param("sesionCajaId") Long sesionCajaId, @Param("usuarioId") Long usuarioId);

    Optional<VentaEntity> findByCodigoFactura(String codigoFactura);
}
