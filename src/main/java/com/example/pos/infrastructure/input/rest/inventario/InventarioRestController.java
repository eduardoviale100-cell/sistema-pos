package com.example.pos.infrastructure.input.rest.inventario;

import com.example.pos.application.usecase.inventario.AjustarStockInventarioUseCase;
import com.example.pos.application.usecase.inventario.ConsultarInventarioUseCase;
import com.example.pos.application.usecase.inventario.ListarInventarioUseCase;
import com.example.pos.application.usecase.inventario.ListarMovimientosKardexUseCase;
import com.example.pos.domain.model.inventario.InventarioDomain;
import com.example.pos.domain.model.inventario.MovimientoInventarioDomain;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de Entrada (Driving / Primary Adapter) REST para el mÃ³dulo de Inventario y Kardex.
 * Expone las operaciones a travÃ©s de HTTP v2 delegando en los casos de uso.
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

