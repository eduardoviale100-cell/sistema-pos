package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.*;
import com.example.pos.domain.model.CajaDomain;
import com.example.pos.domain.model.MovimientoCajaDomain;
import com.example.pos.domain.model.SesionCajaDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador Primario / Conductor (Driving Adapter) REST para el módulo de Caja.
 * Expone las operaciones transaccionales de caja a través de HTTP delegando a casos de uso.
 */
@RestController("cajaRestControllerHexagonal")
@RequestMapping("/api/v2/cajas")
public class CajaRestController {

    private final ListarCajasUseCase listarCajasUseCase;
    private final CrearCajaUseCase crearCajaUseCase;
    private final ActualizarCajaUseCase actualizarCajaUseCase;
    private final EliminarCajaUseCase eliminarCajaUseCase;
    private final AbrirSesionCajaUseCase abrirSesionCajaUseCase;
    private final CerrarSesionCajaUseCase cerrarSesionCajaUseCase;
    private final ObtenerSesionActivaUseCase obtenerSesionActivaUseCase;
    private final RegistrarMovimientoCajaUseCase registrarMovimientoCajaUseCase;
    private final ListarMovimientosCajaUseCase listarMovimientosCajaUseCase;

    public CajaRestController(
            ListarCajasUseCase listarCajasUseCase,
            CrearCajaUseCase crearCajaUseCase,
            ActualizarCajaUseCase actualizarCajaUseCase,
            EliminarCajaUseCase eliminarCajaUseCase,
            AbrirSesionCajaUseCase abrirSesionCajaUseCase,
            CerrarSesionCajaUseCase cerrarSesionCajaUseCase,
            ObtenerSesionActivaUseCase obtenerSesionActivaUseCase,
            RegistrarMovimientoCajaUseCase registrarMovimientoCajaUseCase,
            ListarMovimientosCajaUseCase listarMovimientosCajaUseCase) {
        this.listarCajasUseCase = listarCajasUseCase;
        this.crearCajaUseCase = crearCajaUseCase;
        this.actualizarCajaUseCase = actualizarCajaUseCase;
        this.eliminarCajaUseCase = eliminarCajaUseCase;
        this.abrirSesionCajaUseCase = abrirSesionCajaUseCase;
        this.cerrarSesionCajaUseCase = cerrarSesionCajaUseCase;
        this.obtenerSesionActivaUseCase = obtenerSesionActivaUseCase;
        this.registrarMovimientoCajaUseCase = registrarMovimientoCajaUseCase;
        this.listarMovimientosCajaUseCase = listarMovimientosCajaUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CajaDomain>> listarCajas() {
        return ResponseEntity.ok(listarCajasUseCase.ejecutar());
    }

    @PostMapping
    public ResponseEntity<CajaDomain> crearCaja(@Valid @RequestBody CrearCajaInputDto dto) {
        CajaDomain creada = crearCajaUseCase.ejecutar(dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CajaDomain> actualizarCaja(@PathVariable Long id, @Valid @RequestBody CrearCajaInputDto dto) {
        CajaDomain actualizada = actualizarCajaUseCase.ejecutar(id, dto.getNombre());
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCaja(@PathVariable Long id) {
        eliminarCajaUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/apertura")
    public ResponseEntity<SesionCajaDomain> abrirSesion(@Valid @RequestBody AperturaCajaInputDto dto) {
        SesionCajaDomain sesion = abrirSesionCajaUseCase.ejecutar(
                dto.getCajaId(),
                dto.getUsuarioId(),
                dto.getMontoInicial()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(sesion);
    }

    @PostMapping("/sesiones/{sesionId}/cierre")
    public ResponseEntity<SesionCajaDomain> cerrarSesion(
            @PathVariable Long sesionId,
            @Valid @RequestBody CierreCajaInputDto dto) {
        SesionCajaDomain sesion = cerrarSesionCajaUseCase.ejecutar(sesionId, dto.getMontoReal());
        return ResponseEntity.ok(sesion);
    }

    @GetMapping("/{cajaId}/sesion-activa")
    public ResponseEntity<SesionCajaDomain> obtenerSesionActiva(@PathVariable Long cajaId) {
        return obtenerSesionActivaUseCase.ejecutar(cajaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sesiones/{sesionId}/movimientos")
    public ResponseEntity<MovimientoCajaDomain> registrarMovimiento(
            @PathVariable Long sesionId,
            @Valid @RequestBody MovimientoCajaInputDto dto) {
        MovimientoCajaDomain movimiento = registrarMovimientoCajaUseCase.ejecutar(
                sesionId,
                dto.getUsuarioId(),
                dto.getTipoMovimiento(),
                dto.getMonto(),
                dto.getReferencia(),
                dto.getObservaciones()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }

    @GetMapping("/sesiones/{sesionId}/movimientos")
    public ResponseEntity<List<MovimientoCajaDomain>> listarMovimientos(@PathVariable Long sesionId) {
        return ResponseEntity.ok(listarMovimientosCajaUseCase.ejecutar(sesionId));
    }
}
