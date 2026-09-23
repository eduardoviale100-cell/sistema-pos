package com.example.pos.controller;

import com.example.pos.dto.ClienteRequestDto;
import com.example.pos.model.Cliente;
import com.example.pos.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Integer id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ClienteRequestDto dto) {
        try {
            Cliente nuevoCliente = clienteService.guardarCliente(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Error al crear cliente",
                "mensaje", e.getMessage() != null ? e.getMessage() : e.getClass().getName()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody ClienteRequestDto dto) {
        try {
            Cliente clienteActualizado = clienteService.actualizarCliente(id, dto);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", "Error al actualizar cliente",
                "mensaje", e.getMessage() != null ? e.getMessage() : e.getClass().getName()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
