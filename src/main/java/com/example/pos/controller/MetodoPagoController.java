package com.example.pos.controller;

import com.example.pos.dto.MetodoPagoRequestDto;
import com.example.pos.model.MetodoPago;
import com.example.pos.service.MetodoPagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService metodoPagoService;

    @GetMapping
    public ResponseEntity<List<MetodoPago>> listar(@RequestParam(required = false, defaultValue = "false") boolean soloActivos) {
        if (soloActivos) {
            return ResponseEntity.ok(metodoPagoService.listarActivos());
        }
        return ResponseEntity.ok(metodoPagoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> buscarPorId(@PathVariable Integer id) {
        return metodoPagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetodoPago> crear(@Valid @RequestBody MetodoPagoRequestDto dto) {
        MetodoPago nuevoMetodo = metodoPagoService.guardarMetodoPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMetodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPago> actualizar(@PathVariable Integer id, @Valid @RequestBody MetodoPagoRequestDto dto) {
        MetodoPago actualizado = metodoPagoService.actualizarMetodoPago(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<MetodoPago> cambiarEstado(@PathVariable Integer id, @RequestParam boolean activo) {
        return ResponseEntity.ok(metodoPagoService.cambiarEstado(id, activo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        metodoPagoService.eliminarMetodoPago(id);
        return ResponseEntity.noContent().build();
    }
}
