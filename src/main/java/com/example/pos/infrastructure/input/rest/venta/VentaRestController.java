package com.example.pos.infrastructure.input.rest.venta;

import com.example.pos.application.usecase.venta.AnularVentaUseCase;
import com.example.pos.application.usecase.venta.ListarVentasUseCase;
import com.example.pos.application.usecase.venta.ObtenerVentaPorIdUseCase;
import com.example.pos.application.usecase.venta.RegistrarVentaUseCase;
import com.example.pos.domain.model.venta.VentaDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de Entrada (Driving / Primary Adapter) REST para el mÃ³dulo de Ventas.
 * Expone las operaciones de facturaciÃ³n POS a travÃ©s de HTTP v2 delegando a los Casos de Uso.
 */
@RestController("ventaRestControllerHexagonal")
@RequestMapping("/api/v2/ventas")
public class VentaRestController {

    private final RegistrarVentaUseCase registrarVentaUseCase;
    private final ObtenerVentaPorIdUseCase obtenerVentaPorIdUseCase;
    private final ListarVentasUseCase listarVentasUseCase;
    private final AnularVentaUseCase anularVentaUseCase;

    public VentaRestController(
            RegistrarVentaUseCase registrarVentaUseCase,
            ObtenerVentaPorIdUseCase obtenerVentaPorIdUseCase,
            ListarVentasUseCase listarVentasUseCase,
            AnularVentaUseCase anularVentaUseCase) {
        this.registrarVentaUseCase = registrarVentaUseCase;
        this.obtenerVentaPorIdUseCase = obtenerVentaPorIdUseCase;
        this.listarVentasUseCase = listarVentasUseCase;
        this.anularVentaUseCase = anularVentaUseCase;
    }

    @PostMapping
    public ResponseEntity<VentaDomain> registrarVenta(@Valid @RequestBody RegistrarVentaInputDto dto) {
        VentaDomain ventaGuardada = registrarVentaUseCase.ejecutar(dto.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
    }

    @GetMapping
    public ResponseEntity<List<VentaDomain>> listarVentas(
            @RequestParam(required = false) Long sesionId,
            @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(listarVentasUseCase.ejecutar(sesionId, usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerVentaPorIdUseCase.ejecutar(id));
    }

    @GetMapping("/sesion/{sesionId}")
    public ResponseEntity<List<VentaDomain>> listarPorSesion(
            @PathVariable Long sesionId,
            @RequestParam(required = false) Long usuarioId) {
        return ResponseEntity.ok(listarVentasUseCase.ejecutar(sesionId, usuarioId));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<VentaDomain> anularVenta(
            @PathVariable Long id,
            @RequestBody(required = false) AnularVentaInputDto dto) {
        String motivo = (dto != null) ? dto.getMotivo() : null;
        return ResponseEntity.ok(anularVentaUseCase.ejecutar(id, motivo));
    }
}

