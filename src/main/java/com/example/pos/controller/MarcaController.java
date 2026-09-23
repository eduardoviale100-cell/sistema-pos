package com.example.pos.controller;

import com.example.pos.dto.MarcaRequestDto;
import com.example.pos.model.Marca;
import com.example.pos.service.MarcaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    public ResponseEntity<List<Marca>> listar() {
        return ResponseEntity.ok(marcaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marca> buscarPorId(@PathVariable Integer id) {
        return marcaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody MarcaRequestDto dto) {
        try {
            Marca nuevaMarca = marcaService.guardarMarca(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMarca);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of(
                "error", "Error al crear la marca",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Error desconocido"
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody MarcaRequestDto dto) {
        try {
            Marca marcaActualizada = marcaService.actualizarMarca(id, dto);
            return ResponseEntity.ok(marcaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of(
                "error", "Marca no encontrada",
                "mensaje", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of(
                "error", "Error al actualizar la marca",
                "mensaje", e.getMessage() != null ? e.getMessage() : "Error desconocido"
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        marcaService.eliminarMarca(id);
        return ResponseEntity.noContent().build();
    }
}
