package com.example.pos.controller;

import com.example.pos.dto.CompraRequestDto;
import com.example.pos.model.Compra;
import com.example.pos.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @GetMapping
    public ResponseEntity<List<Compra>> listar() {
        return ResponseEntity.ok(compraService.listarCompras());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> buscarPorId(@PathVariable Integer id) {
        return compraService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> registrarCompra(@Valid @RequestBody CompraRequestDto dto) {
        try {
            Compra nuevaCompra = compraService.registrarCompra(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCompra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Error al registrar compra",
                "mensaje", e.getMessage() != null ? e.getMessage() : e.getClass().getName()
            ));
        }
    }
}
