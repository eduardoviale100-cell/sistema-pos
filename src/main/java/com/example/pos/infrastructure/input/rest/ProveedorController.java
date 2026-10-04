package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.ActualizarProveedorCommand;
import com.example.pos.application.usecase.ActualizarProveedorUseCase;
import com.example.pos.application.usecase.BuscarProveedorPorIdUseCase;
import com.example.pos.application.usecase.CrearProveedorCommand;
import com.example.pos.application.usecase.CrearProveedorUseCase;
import com.example.pos.application.usecase.EliminarProveedorUseCase;
import com.example.pos.application.usecase.ListarProveedoresUseCase;
import com.example.pos.domain.model.ProveedorDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Adaptador de Entrada (Driving/Primary Adapter) REST para el módulo Proveedor.
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>GET    /api/proveedores        → Listar todos los proveedores</li>
 *   <li>GET    /api/proveedores/{id}   → Buscar proveedor por ID</li>
 *   <li>POST   /api/proveedores        → Registrar nuevo proveedor</li>
 *   <li>PUT    /api/proveedores/{id}   → Actualizar proveedor existente</li>
 *   <li>DELETE /api/proveedores/{id}   → Eliminar proveedor</li>
 * </ul>
 * </p>
 */
@RestController("proveedorHexagonalController")
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final CrearProveedorUseCase       crearProveedorUseCase;
    private final ListarProveedoresUseCase     listarProveedoresUseCase;
    private final BuscarProveedorPorIdUseCase buscarProveedorPorIdUseCase;
    private final ActualizarProveedorUseCase  actualizarProveedorUseCase;
    private final EliminarProveedorUseCase    eliminarProveedorUseCase;

    public ProveedorController(CrearProveedorUseCase       crearProveedorUseCase,
                               ListarProveedoresUseCase     listarProveedoresUseCase,
                               BuscarProveedorPorIdUseCase buscarProveedorPorIdUseCase,
                               ActualizarProveedorUseCase  actualizarProveedorUseCase,
                               EliminarProveedorUseCase    eliminarProveedorUseCase) {
        this.crearProveedorUseCase       = crearProveedorUseCase;
        this.listarProveedoresUseCase     = listarProveedoresUseCase;
        this.buscarProveedorPorIdUseCase = buscarProveedorPorIdUseCase;
        this.actualizarProveedorUseCase  = actualizarProveedorUseCase;
        this.eliminarProveedorUseCase    = eliminarProveedorUseCase;
    }

    // =========================================================================
    // GET /api/proveedores — Listar todos los proveedores
    // =========================================================================

    @GetMapping
    public ResponseEntity<List<ProveedorDomain>> listarTodos() {
        return ResponseEntity.ok(listarProveedoresUseCase.ejecutar());
    }

    // =========================================================================
    // GET /api/proveedores/{id} — Buscar proveedor por ID
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarProveedorPorIdUseCase.ejecutar(id));
    }

    // =========================================================================
    // POST /api/proveedores — Registrar nuevo proveedor
    // =========================================================================

    @PostMapping
    public ResponseEntity<ProveedorDomain> crear(@Valid @RequestBody ProveedorRequestDto dto) {
        CrearProveedorCommand command = new CrearProveedorCommand(
                dto.getNombre(),
                dto.getRucDni(),
                dto.getTelefono(),
                dto.getEmail(),
                dto.getDireccion()
        );

        ProveedorDomain nuevoProveedor = crearProveedorUseCase.ejecutar(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    // =========================================================================
    // PUT /api/proveedores/{id} — Actualizar datos de un proveedor
    // =========================================================================

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDomain> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarProveedorRequestDto dto) {

        ActualizarProveedorCommand command = new ActualizarProveedorCommand(
                dto.getNombre(),
                dto.getTelefono(),
                dto.getEmail(),
                dto.getDireccion()
        );

        ProveedorDomain proveedorActualizado = actualizarProveedorUseCase.ejecutar(id, command);
        return ResponseEntity.ok(proveedorActualizado);
    }

    // =========================================================================
    // DELETE /api/proveedores/{id} — Eliminar proveedor
    // =========================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        eliminarProveedorUseCase.ejecutar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Proveedor eliminado exitosamente."));
    }
}
