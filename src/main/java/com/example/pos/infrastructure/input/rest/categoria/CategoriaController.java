package com.example.pos.infrastructure.input.rest.categoria;

import com.example.pos.application.usecase.categoria.ActualizarCategoriaCommand;
import com.example.pos.application.usecase.categoria.ActualizarCategoriaUseCase;
import com.example.pos.application.usecase.categoria.BuscarCategoriaPorIdUseCase;
import com.example.pos.application.usecase.categoria.CrearCategoriaCommand;
import com.example.pos.application.usecase.categoria.CrearCategoriaUseCase;
import com.example.pos.application.usecase.categoria.EliminarCategoriaUseCase;
import com.example.pos.application.usecase.categoria.ListarCategoriasUseCase;
import com.example.pos.domain.model.categoria.CategoriaDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Adaptador de Entrada (Driving/Primary Adapter) REST para el mÃ³dulo de CategorÃ­a.
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>GET    /api/categorias        â†’ Listar todas las categorÃ­as</li>
 *   <li>GET    /api/categorias/{id}   â†’ Buscar categorÃ­a por ID</li>
 *   <li>POST   /api/categorias        â†’ Registrar nueva categorÃ­a</li>
 *   <li>PUT    /api/categorias/{id}   â†’ Actualizar categorÃ­a existente</li>
 *   <li>DELETE /api/categorias/{id}   â†’ Eliminar categorÃ­a</li>
 * </ul>
 * </p>
 */
@RestController("categoriaHexagonalController")
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CrearCategoriaUseCase       crearCategoriaUseCase;
    private final ListarCategoriasUseCase     listarCategoriasUseCase;
    private final BuscarCategoriaPorIdUseCase buscarCategoriaPorIdUseCase;
    private final ActualizarCategoriaUseCase  actualizarCategoriaUseCase;
    private final EliminarCategoriaUseCase    eliminarCategoriaUseCase;

    public CategoriaController(CrearCategoriaUseCase       crearCategoriaUseCase,
                               ListarCategoriasUseCase     listarCategoriasUseCase,
                               BuscarCategoriaPorIdUseCase buscarCategoriaPorIdUseCase,
                               ActualizarCategoriaUseCase  actualizarCategoriaUseCase,
                               EliminarCategoriaUseCase    eliminarCategoriaUseCase) {
        this.crearCategoriaUseCase       = crearCategoriaUseCase;
        this.listarCategoriasUseCase     = listarCategoriasUseCase;
        this.buscarCategoriaPorIdUseCase = buscarCategoriaPorIdUseCase;
        this.actualizarCategoriaUseCase  = actualizarCategoriaUseCase;
        this.eliminarCategoriaUseCase    = eliminarCategoriaUseCase;
    }

    // =========================================================================
    // GET /api/categorias â€” Listar todas las categorÃ­as
    // =========================================================================

    @GetMapping
    public ResponseEntity<List<CategoriaDomain>> listarTodas() {
        return ResponseEntity.ok(listarCategoriasUseCase.ejecutar());
    }

    // =========================================================================
    // GET /api/categorias/{id} â€” Buscar categorÃ­a por ID
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarCategoriaPorIdUseCase.ejecutar(id));
    }

    // =========================================================================
    // POST /api/categorias â€” Registrar nueva categorÃ­a
    // =========================================================================

    @PostMapping
    public ResponseEntity<CategoriaDomain> crear(@Valid @RequestBody CategoriaRequestDto dto) {
        CrearCategoriaCommand command = new CrearCategoriaCommand(
                dto.getNombre(),
                dto.getDescripcion()
        );

        CategoriaDomain nuevaCategoria = crearCategoriaUseCase.ejecutar(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
    }

    // =========================================================================
    // PUT /api/categorias/{id} â€” Actualizar categorÃ­a existente
    // =========================================================================

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDomain> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDto dto) {

        ActualizarCategoriaCommand command = new ActualizarCategoriaCommand(
                dto.getNombre(),
                dto.getDescripcion()
        );

        CategoriaDomain categoriaActualizada = actualizarCategoriaUseCase.ejecutar(id, command);
        return ResponseEntity.ok(categoriaActualizada);
    }

    // =========================================================================
    // DELETE /api/categorias/{id} â€” Eliminar categorÃ­a
    // =========================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        eliminarCategoriaUseCase.ejecutar(id);
        return ResponseEntity.ok(Map.of("mensaje", "CategorÃ­a eliminada exitosamente."));
    }
}

