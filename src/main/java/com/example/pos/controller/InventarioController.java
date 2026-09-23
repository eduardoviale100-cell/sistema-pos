package com.example.pos.controller;

import com.example.pos.dto.AjusteStockRequestDto;
import com.example.pos.model.Inventario;
import com.example.pos.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<Inventario>> listar() {
        return ResponseEntity.ok(inventarioService.listarTodo());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Inventario> buscarPorProductoId(@PathVariable Integer productoId) {
        return inventarioService.buscarPorProductoId(productoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alertas-bajo-stock")
    public ResponseEntity<List<Inventario>> listarBajoStock() {
        return ResponseEntity.ok(inventarioService.listarBajoStock());
    }

    @PatchMapping("/producto/{productoId}/ajuste")
    public ResponseEntity<?> ajustarStock(
            @PathVariable Integer productoId,
            @Valid @RequestBody AjusteStockRequestDto dto) {
        try {
            Inventario inventarioActualizado = inventarioService.ajustarStock(productoId, dto);
            return ResponseEntity.ok(inventarioActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/producto/{productoId}/movimientos")
    public ResponseEntity<List<com.example.pos.model.MovimientoInventario>> listarMovimientosPorProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(inventarioService.listarMovimientosPorProducto(productoId));
    }
}
