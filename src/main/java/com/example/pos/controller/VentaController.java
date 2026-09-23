package com.example.pos.controller;

import com.example.pos.dto.VentaRequestDto;
import com.example.pos.model.Venta;
import com.example.pos.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Venta>> listar(
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) Integer sesionId) {
        if (sesionId != null && usuarioId != null) {
            return ResponseEntity.ok(ventaService.listarPorSesionYUsuario(sesionId, usuarioId));
        } else if (sesionId != null) {
            return ResponseEntity.ok(ventaService.listarPorSesionCaja(sesionId));
        } else if (usuarioId != null) {
            return ResponseEntity.ok(ventaService.listarPorUsuario(usuarioId));
        }
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> buscarPorId(@PathVariable Integer id) {
        return ventaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sesion/{sesionId}")
    public ResponseEntity<List<Venta>> listarPorSesion(
            @PathVariable Integer sesionId,
            @RequestParam(required = false) Integer usuarioId) {
        if (usuarioId != null) {
            return ResponseEntity.ok(ventaService.listarPorSesionYUsuario(sesionId, usuarioId));
        }
        return ResponseEntity.ok(ventaService.listarPorSesionCaja(sesionId));
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(@Valid @RequestBody VentaRequestDto dto) {
        try {
            Venta ventaGuardada = ventaService.registrarVenta(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Datos de Venta Inválidos",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Argumento inválido"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of(
                "error", "Conflicto de Estado",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Estado inconsistente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Error en la Venta",
                "mensaje", e.getMessage() != null ? e.getMessage() : e.getClass().getName()
            ));
        }
    }
}
