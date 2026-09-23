package com.example.pos.controller;

import com.example.pos.dto.AperturaCajaDto;
import com.example.pos.dto.CajaRequestDto;
import com.example.pos.dto.CierreCajaDto;
import com.example.pos.dto.MovimientoCajaRequestDto;
import com.example.pos.model.Caja;
import com.example.pos.model.MovimientoCaja;
import com.example.pos.model.SesionCaja;
import com.example.pos.service.CajaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cajas")
public class CajaController {

    @Autowired
    private CajaService cajaService;

    @GetMapping
    public ResponseEntity<List<Caja>> listarCajas() {
        return ResponseEntity.ok(cajaService.listarCajas());
    }

    @PostMapping
    public ResponseEntity<Caja> crearCaja(@Valid @RequestBody CajaRequestDto dto) {
        Caja nuevaCaja = cajaService.crearCaja(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCaja);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCaja(@PathVariable Integer id, @Valid @RequestBody CajaRequestDto dto) {
        try {
            Caja actualizada = cajaService.actualizarCaja(id, dto);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCaja(@PathVariable Integer id) {
        try {
            cajaService.eliminarCaja(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/apertura")
    public ResponseEntity<?> abrirSesion(@Valid @RequestBody AperturaCajaDto dto) {
        try {
            SesionCaja sesion = cajaService.abrirSesion(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(sesion);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of(
                "error", "Conflicto de Caja",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Estado inconsistente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Error al abrir sesión",
                "mensaje", e.getMessage() != null ? e.getMessage() : e.getClass().getName()
            ));
        }
    }

    @PostMapping("/sesiones/{sesionId}/cierre")
    public ResponseEntity<?> cerrarSesion(
            @PathVariable Integer sesionId,
            @Valid @RequestBody CierreCajaDto dto) {
        try {
            SesionCaja sesion = cajaService.cerrarSesion(sesionId, dto);
            return ResponseEntity.ok(sesion);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of(
                "error", "Conflicto al Cerrar Caja",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Estado inconsistente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Error al cerrar sesión",
                "mensaje", e.getMessage() != null ? e.getMessage() : e.getClass().getName()
            ));
        }
    }

    @GetMapping("/{cajaId}/sesion-activa")
    public ResponseEntity<SesionCaja> obtenerSesionActiva(@PathVariable Integer cajaId) {
        return cajaService.obtenerSesionActiva(cajaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sesiones/{sesionId}/movimientos")
    public ResponseEntity<?> registrarMovimiento(
            @PathVariable Integer sesionId,
            @Valid @RequestBody MovimientoCajaRequestDto dto) {
        try {
            MovimientoCaja movimiento = cajaService.registrarMovimiento(sesionId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Datos de Movimiento Inválidos",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Argumento inválido"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of(
                "error", "Conflicto de Estado",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Estado inconsistente"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Error en Movimiento de Caja",
                "mensaje", e.getMessage() != null ? e.getMessage() : e.getClass().getName()
            ));
        }
    }

    @GetMapping("/sesiones/{sesionId}/movimientos")
    public ResponseEntity<List<MovimientoCaja>> listarMovimientos(@PathVariable Integer sesionId) {
        return ResponseEntity.ok(cajaService.listarMovimientosDeSesion(sesionId));
    }
}
