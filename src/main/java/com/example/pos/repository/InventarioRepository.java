package com.example.pos.repository;

import com.example.pos.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    Optional<Inventario> findByProductoId(Integer productoId);

    @Query("SELECT i FROM Inventario i JOIN FETCH i.producto WHERE i.stockActual <= i.stockMinimo")
    List<Inventario> buscarProductosConBajoStock();
}
