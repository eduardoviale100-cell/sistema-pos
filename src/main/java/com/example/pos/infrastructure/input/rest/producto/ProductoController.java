package com.example.pos.infrastructure.input.rest.producto;

import com.example.pos.application.usecase.producto.ActualizarProductoCommand;
import com.example.pos.application.usecase.producto.ActualizarProductoUseCase;
import com.example.pos.application.usecase.producto.BuscarProductoPorIdUseCase;
import com.example.pos.application.usecase.producto.CrearProductoCommand;
import com.example.pos.application.usecase.producto.CrearProductoUseCase;
import com.example.pos.application.usecase.producto.DesactivarProductoUseCase;
import com.example.pos.application.usecase.producto.ListarProductosUseCase;
import com.example.pos.domain.model.producto.ProductoDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Adaptador de Entrada (Driving/Primary Adapter) REST para el mÃ³dulo de Producto.
 *
 * <p>Recibe solicitudes HTTP externas, valida la carga Ãºtil mediante Bean Validation
 * y delega la ejecuciÃ³n a los casos de uso correspondientes de la capa de aplicaciÃ³n.
 * El manejo de excepciones de dominio es responsabilidad del
 * {@link ProductoRestExceptionHandler} global.</p>
 *
 * <p><strong>Principio de separaciÃ³n estricta:</strong> este controlador nunca
 * accede directamente a repositorios, entidades JPA ni al modelo de dominio sin
 * pasar por los casos de uso.</p>
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>POST   /api/productos       â†’ Registrar nuevo producto</li>
 *   <li>GET    /api/productos       â†’ Listar todos los productos</li>
 *   <li>GET    /api/productos/{id}  â†’ Buscar producto por ID</li>
 *   <li>PUT    /api/productos/{id}  â†’ Actualizar datos de un producto</li>
 *   <li>DELETE /api/productos/{id}  â†’ Desactivar (baja lÃ³gica) producto</li>
 * </ul>
 * </p>
 */
@RestController("productoHexagonalController")
@RequestMapping("/api/productos")
public class ProductoController {

    private final CrearProductoUseCase       crearProductoUseCase;
    private final ListarProductosUseCase     listarProductosUseCase;
    private final BuscarProductoPorIdUseCase buscarProductoPorIdUseCase;
    private final ActualizarProductoUseCase  actualizarProductoUseCase;
    private final DesactivarProductoUseCase  desactivarProductoUseCase;

    /**
     * InyecciÃ³n por constructor de todos los casos de uso requeridos por este adaptador.
     */
    public ProductoController(CrearProductoUseCase       crearProductoUseCase,
                              ListarProductosUseCase     listarProductosUseCase,
                              BuscarProductoPorIdUseCase buscarProductoPorIdUseCase,
                              ActualizarProductoUseCase  actualizarProductoUseCase,
                              DesactivarProductoUseCase  desactivarProductoUseCase) {
        this.crearProductoUseCase       = crearProductoUseCase;
        this.listarProductosUseCase     = listarProductosUseCase;
        this.buscarProductoPorIdUseCase = buscarProductoPorIdUseCase;
        this.actualizarProductoUseCase  = actualizarProductoUseCase;
        this.desactivarProductoUseCase  = desactivarProductoUseCase;
    }

    // =========================================================================
    // POST /api/productos â€” Registrar un nuevo producto
    // =========================================================================

    /**
     * Registra un nuevo producto en el sistema.
     *
     * @param dto Datos del producto validados mediante Bean Validation.
     * @return 201 CREATED con el {@link ProductoDomain} persistido.
     *         409 CONFLICT si ya existe un producto con el mismo cÃ³digo
     *         (manejado por {@link ProductoRestExceptionHandler}).
     */
    @PostMapping
    public ResponseEntity<ProductoDomain> crear(@Valid @RequestBody CrearProductoRequestDto dto) {
        CrearProductoCommand command = new CrearProductoCommand(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getPrecioCompra(),
                dto.getPrecioVenta(),
                dto.getCategoriaId(),
                dto.getMarcaId(),
                dto.getProveedorId()
        );

        ProductoDomain nuevoProducto = crearProductoUseCase.ejecutar(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // =========================================================================
    // GET /api/productos â€” Listar todos los productos
    // =========================================================================

    /**
     * Retorna el listado completo de productos registrados en el sistema.
     *
     * @return 200 OK con la lista de {@link ProductoDomain}.
     *         La lista puede estar vacÃ­a pero nunca serÃ¡ {@code null}.
     */
    @GetMapping
    public ResponseEntity<List<ProductoDomain>> listarTodos() {
        List<ProductoDomain> productos = listarProductosUseCase.ejecutar();
        return ResponseEntity.ok(productos);
    }

    // =========================================================================
    // GET /api/productos/{id} â€” Buscar un producto por su ID
    // =========================================================================

    /**
     * Busca y retorna un producto especÃ­fico por su identificador Ãºnico.
     *
     * @param id Identificador Ãºnico del producto (path variable).
     * @return 200 OK con el {@link ProductoDomain} encontrado.
     *         404 NOT_FOUND si no existe un producto con el ID indicado
     *         (manejado por {@link ProductoRestExceptionHandler}).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDomain> buscarPorId(@PathVariable Long id) {
        ProductoDomain producto = buscarProductoPorIdUseCase.ejecutar(id);
        return ResponseEntity.ok(producto);
    }

    // =========================================================================
    // PUT /api/productos/{id} â€” Actualizar datos de un producto
    // =========================================================================

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id Identificador Ãºnico del producto a actualizar.
     * @param dto Datos a actualizar validados por Bean Validation.
     * @return 200 OK con el {@link ProductoDomain} actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDomain> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarProductoRequestDto dto) {

        ActualizarProductoCommand command = new ActualizarProductoCommand(
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getPrecioCompra(),
                dto.getPrecioVenta(),
                dto.getStock(),
                dto.getEstado(),
                dto.getCategoriaId(),
                dto.getMarcaId(),
                dto.getProveedorId()
        );

        ProductoDomain productoActualizado = actualizarProductoUseCase.ejecutar(id, command);
        return ResponseEntity.ok(productoActualizado);
    }

    // =========================================================================
    // DELETE /api/productos/{id} â€” Desactivar (baja lÃ³gica) un producto
    // =========================================================================

    /**
     * Desactiva lÃ³gicamente un producto del sistema (soft delete).
     *
     * @param id Identificador Ãºnico del producto a desactivar.
     * @return 200 OK con confirmaciÃ³n y el producto desactivado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> desactivar(@PathVariable Long id) {
        ProductoDomain productoDesactivado = desactivarProductoUseCase.ejecutar(id);

        return ResponseEntity.ok(Map.of(
                "mensaje", "El producto ha sido desactivado exitosamente.",
                "productoId", productoDesactivado.getId(),
                "estado", productoDesactivado.getEstado()
        ));
    }
}

