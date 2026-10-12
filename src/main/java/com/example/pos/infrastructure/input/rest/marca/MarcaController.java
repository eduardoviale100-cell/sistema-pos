package com.example.pos.infrastructure.input.rest.marca;

import com.example.pos.application.usecase.marca.ActualizarMarcaCommand;
import com.example.pos.application.usecase.marca.ActualizarMarcaUseCase;
import com.example.pos.application.usecase.marca.BuscarMarcaPorIdUseCase;
import com.example.pos.application.usecase.marca.CrearMarcaCommand;
import com.example.pos.application.usecase.marca.CrearMarcaUseCase;
import com.example.pos.application.usecase.marca.EliminarMarcaUseCase;
import com.example.pos.application.usecase.marca.ListarMarcasUseCase;
import com.example.pos.domain.model.marca.MarcaDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Adaptador de Entrada (Driving/Primary Adapter) REST para el mÃ³dulo de Marca.
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>GET    /api/marcas        â†’ Listar todas las marcas</li>
 *   <li>GET    /api/marcas/{id}   â†’ Buscar marca por ID</li>
 *   <li>POST   /api/marcas        â†’ Registrar nueva marca</li>
 *   <li>PUT    /api/marcas/{id}   â†’ Actualizar marca existente</li>
 *   <li>DELETE /api/marcas/{id}   â†’ Eliminar marca</li>
 * </ul>
 * </p>
 */
@RestController("marcaHexagonalController")
@RequestMapping("/api/marcas")
public class MarcaController {

    private final CrearMarcaUseCase       crearMarcaUseCase;
    private final ListarMarcasUseCase     listarMarcasUseCase;
    private final BuscarMarcaPorIdUseCase buscarMarcaPorIdUseCase;
    private final ActualizarMarcaUseCase  actualizarMarcaUseCase;
    private final EliminarMarcaUseCase    eliminarMarcaUseCase;

    public MarcaController(CrearMarcaUseCase       crearMarcaUseCase,
                           ListarMarcasUseCase     listarMarcasUseCase,
                           BuscarMarcaPorIdUseCase buscarMarcaPorIdUseCase,
                           ActualizarMarcaUseCase  actualizarMarcaUseCase,
                           EliminarMarcaUseCase    eliminarMarcaUseCase) {
        this.crearMarcaUseCase       = crearMarcaUseCase;
        this.listarMarcasUseCase     = listarMarcasUseCase;
        this.buscarMarcaPorIdUseCase = buscarMarcaPorIdUseCase;
        this.actualizarMarcaUseCase  = actualizarMarcaUseCase;
        this.eliminarMarcaUseCase    = eliminarMarcaUseCase;
    }

    // =========================================================================
    // GET /api/marcas â€” Listar todas las marcas
    // =========================================================================

    @GetMapping
    public ResponseEntity<List<MarcaDomain>> listarTodas() {
        return ResponseEntity.ok(listarMarcasUseCase.ejecutar());
    }

    // =========================================================================
    // GET /api/marcas/{id} â€” Buscar marca por ID
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<MarcaDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarMarcaPorIdUseCase.ejecutar(id));
    }

    // =========================================================================
    // POST /api/marcas â€” Registrar nueva marca
    // =========================================================================

    @PostMapping
    public ResponseEntity<MarcaDomain> crear(@Valid @RequestBody MarcaRequestDto dto) {
        CrearMarcaCommand command = new CrearMarcaCommand(
                dto.getNombre(),
                dto.getDescripcion()
        );

        MarcaDomain nuevaMarca = crearMarcaUseCase.ejecutar(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMarca);
    }

    // =========================================================================
    // PUT /api/marcas/{id} â€” Actualizar marca existente
    // =========================================================================

    @PutMapping("/{id}")
    public ResponseEntity<MarcaDomain> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MarcaRequestDto dto) {

        ActualizarMarcaCommand command = new ActualizarMarcaCommand(
                dto.getNombre(),
                dto.getDescripcion()
        );

        MarcaDomain marcaActualizada = actualizarMarcaUseCase.ejecutar(id, command);
        return ResponseEntity.ok(marcaActualizada);
    }

    // =========================================================================
    // DELETE /api/marcas/{id} â€” Eliminar marca
    // =========================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        eliminarMarcaUseCase.ejecutar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Marca eliminada exitosamente."));
    }
}

