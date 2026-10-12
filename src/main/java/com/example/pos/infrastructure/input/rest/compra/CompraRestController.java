package com.example.pos.infrastructure.input.rest.compra;

import com.example.pos.application.usecase.compra.ListarComprasUseCase;
import com.example.pos.application.usecase.compra.ObtenerCompraPorIdUseCase;
import com.example.pos.application.usecase.compra.RegistrarCompraUseCase;
import com.example.pos.domain.model.compra.CompraDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador de Entrada (Driving / Primary Adapter) REST para el mÃ³dulo de Compras.
 * Expone la recepciÃ³n y consulta de Ã³rdenes de compra a proveedores en /api/v2/compras.
 */
@RestController("compraRestControllerHexagonal")
@RequestMapping("/api/v2/compras")
public class CompraRestController {

    private final RegistrarCompraUseCase registrarCompraUseCase;
    private final ListarComprasUseCase listarComprasUseCase;
    private final ObtenerCompraPorIdUseCase obtenerCompraPorIdUseCase;

    public CompraRestController(
            RegistrarCompraUseCase registrarCompraUseCase,
            ListarComprasUseCase listarComprasUseCase,
            ObtenerCompraPorIdUseCase obtenerCompraPorIdUseCase) {
        this.registrarCompraUseCase = registrarCompraUseCase;
        this.listarComprasUseCase = listarComprasUseCase;
        this.obtenerCompraPorIdUseCase = obtenerCompraPorIdUseCase;
    }

    @PostMapping
    public ResponseEntity<CompraDomain> registrarCompra(@Valid @RequestBody RegistrarCompraInputDto dto) {
        CompraDomain compraGuardada = registrarCompraUseCase.ejecutar(dto.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(compraGuardada);
    }

    @GetMapping
    public ResponseEntity<List<CompraDomain>> listarCompras() {
        return ResponseEntity.ok(listarComprasUseCase.ejecutar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerCompraPorIdUseCase.ejecutar(id));
    }
}

