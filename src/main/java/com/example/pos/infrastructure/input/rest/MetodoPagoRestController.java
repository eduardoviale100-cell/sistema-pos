package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.*;
import com.example.pos.domain.model.MetodoPagoDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST hexagonal para el módulo MetodoPago.
 * Bean name diferenciado para coexistir con el controlador MVC legacy.
 */
@RestController("metodoPagoControllerHexagonal")
@RequestMapping("/api/v2/metodos-pago")
public class MetodoPagoRestController {

    private final CrearMetodoPagoUseCase crearMetodoPagoUseCase;
    private final ActualizarMetodoPagoUseCase actualizarMetodoPagoUseCase;
    private final CambiarEstadoMetodoPagoUseCase cambiarEstadoMetodoPagoUseCase;
    private final ListarMetodosPagoUseCase listarMetodosPagoUseCase;
    private final BuscarMetodoPagoPorIdUseCase buscarMetodoPagoPorIdUseCase;
    private final EliminarMetodoPagoUseCase eliminarMetodoPagoUseCase;

    public MetodoPagoRestController(
            CrearMetodoPagoUseCase crearMetodoPagoUseCase,
            ActualizarMetodoPagoUseCase actualizarMetodoPagoUseCase,
            CambiarEstadoMetodoPagoUseCase cambiarEstadoMetodoPagoUseCase,
            ListarMetodosPagoUseCase listarMetodosPagoUseCase,
            BuscarMetodoPagoPorIdUseCase buscarMetodoPagoPorIdUseCase,
            EliminarMetodoPagoUseCase eliminarMetodoPagoUseCase) {
        this.crearMetodoPagoUseCase = crearMetodoPagoUseCase;
        this.actualizarMetodoPagoUseCase = actualizarMetodoPagoUseCase;
        this.cambiarEstadoMetodoPagoUseCase = cambiarEstadoMetodoPagoUseCase;
        this.listarMetodosPagoUseCase = listarMetodosPagoUseCase;
        this.buscarMetodoPagoPorIdUseCase = buscarMetodoPagoPorIdUseCase;
        this.eliminarMetodoPagoUseCase = eliminarMetodoPagoUseCase;
    }

    // ─── GET /api/v2/metodos-pago ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<MetodoPagoDomain>> listar(
            @RequestParam(required = false) Boolean soloActivos) {
        return ResponseEntity.ok(listarMetodosPagoUseCase.ejecutar(Boolean.TRUE.equals(soloActivos)));
    }

    // ─── GET /api/v2/metodos-pago/{id} ────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarMetodoPagoPorIdUseCase.ejecutar(id));
    }

    // ─── POST /api/v2/metodos-pago ────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<MetodoPagoDomain> crear(@Valid @RequestBody MetodoPagoRequestDto dto) {
        CrearMetodoPagoCommand command = new CrearMetodoPagoCommand(
                dto.getNombre(),
                dto.getActivo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(crearMetodoPagoUseCase.ejecutar(command));
    }

    // ─── PUT /api/v2/metodos-pago/{id} ────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<MetodoPagoDomain> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MetodoPagoRequestDto dto) {
        ActualizarMetodoPagoCommand command = new ActualizarMetodoPagoCommand(
                dto.getNombre(),
                dto.getActivo()
        );
        return ResponseEntity.ok(actualizarMetodoPagoUseCase.ejecutar(id, command));
    }

    // ─── PATCH /api/v2/metodos-pago/{id}/estado ───────────────────────────────
    @PatchMapping("/{id}/estado")
    public ResponseEntity<MetodoPagoDomain> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        return ResponseEntity.ok(cambiarEstadoMetodoPagoUseCase.ejecutar(id, activo));
    }

    // ─── DELETE /api/v2/metodos-pago/{id} ─────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarMetodoPagoUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }
}
