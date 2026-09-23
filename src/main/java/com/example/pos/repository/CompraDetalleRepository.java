package com.example.pos.repository;

import com.example.pos.model.CompraDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraDetalleRepository extends JpaRepository<CompraDetalle, Integer> {
    List<CompraDetalle> findByCompraId(Integer compraId);
}
