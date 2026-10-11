package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.AjustarStockInventarioUseCase;
import com.example.pos.application.usecase.ConsultarInventarioUseCase;
import com.example.pos.application.usecase.ListarInventarioUseCase;
import com.example.pos.application.usecase.ListarMovimientosKardexUseCase;
import com.example.pos.domain.model.InventarioDomain;
import com.example.pos.domain.model.MovimientoInventarioDomain;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de Entrada (Driving / Primary Adapter) REST para el módulo de Inventario y Kardex.
 * Expone las operaciones a través de HTTP v2 delegando en los casos de uso.
 */
@RestController("inventarioRestControllerHexagonal")
@RequestMapping("/api/v2/inventario")
public class InventarioRestController {

    private final ConsultarInventarioUseCase consultarInventarioUseCase;
    private final AjustarStockInventarioUseCase ajustarStockInventarioUseCase;
    private final ListarInventarioUseCase listarInventarioUseCase;
    private final ListarMovimientosKardexUseCase listarMovimientosKardexUseCase;

    public InventarioRestController(
            ConsultarInventarioUseCase consultarInventarioUseCase,
            AjustarStockInventarioUseCase ajustarStockInventarioUseCase,
            ListarInventarioUseCase listarInventarioUseCase,
            ListarMovimientosKardexUseCase listarMovimientosKardexUseCase) {
        this.consultarInventarioUseCase = consultarInventarioUseCase;
        this.ajustarStockInventarioUseCase = ajustarStockInventarioUseCase;
        this.listarInventarioUseCase = listarInventarioUseCase;
        this.listarMovimientosKardexUseCase = listarMovimientosKardexUseCase;
    }

    @GetMapping
    public ResponseEntity<List<InventarioDomain>> listarTodo() {
        return ResponseEntity.ok(listarInventarioUseCase.listarTodos());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioDomain> buscarPorProductoId(@PathVariable Long productoId) {
        return ResponseEntity.ok(consultarInventarioUseCase.ejecutar(productoId));
    }

    @GetMapping("/alertas-bajo-stock")
    public ResponseEntity<List<InventarioDomain>> listarBajoStock() {
        return ResponseEntity.ok(listarInventarioUseCase.listarBajoStock());
    }

    @RequestMapping(value = "/producto/{productoId}/ajuste", method = {RequestMethod.PATCH, RequestMethod.POST})
    public ResponseEntity<InventarioDomain> ajustarStock(
            @PathVariable Long productoId,
            @Valid @RequestBody AjustarStockInputDto dto) {
        InventarioDomain actualizado = ajustarStockInventarioUseCase.ejecutar(dto.toCommand(productoId));
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/producto/{productoId}/movimientos")
    public ResponseEntity<List<MovimientoInventarioDomain>> listarMovimientosPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(listarMovimientosKardexUseCase.listarPorProductoId(productoId));
    }

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoInventarioDomain>> listarTodosLosMovimientos() {
        return ResponseEntity.ok(listarMovimientosKardexUseCase.listarTodos());
    }
}
