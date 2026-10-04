package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.ActualizarClienteCommand;
import com.example.pos.application.usecase.ActualizarClienteUseCase;
import com.example.pos.application.usecase.BuscarClientePorIdUseCase;
import com.example.pos.application.usecase.CrearClienteCommand;
import com.example.pos.application.usecase.CrearClienteUseCase;
import com.example.pos.application.usecase.DesactivarClienteUseCase;
import com.example.pos.application.usecase.ListarClientesUseCase;
import com.example.pos.domain.model.ClienteDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Adaptador de Entrada (Driving/Primary Adapter) REST para el módulo de Cliente.
 *
 * <p>Recibe solicitudes HTTP externas, valida la carga útil mediante Bean Validation
 * y delega la ejecución a los casos de uso correspondientes de la capa de aplicación.
 * El manejo de excepciones de dominio ({@code ClienteNoEncontradoException},
 * {@code ClienteDuplicadoException}, etc.) es responsabilidad del
 * {@link ClienteRestExceptionHandler} global.</p>
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>GET    /api/clientes        → Listar todos los clientes</li>
 *   <li>GET    /api/clientes/{id}   → Buscar cliente por ID</li>
 *   <li>POST   /api/clientes        → Registrar nuevo cliente</li>
 *   <li>PUT    /api/clientes/{id}   → Actualizar cliente existente</li>
 *   <li>DELETE /api/clientes/{id}   → Desactivar (baja lógica) cliente</li>
 * </ul>
 * </p>
 */
@RestController("clienteHexagonalController")
@RequestMapping("/api/clientes")
public class ClienteController {

    private final CrearClienteUseCase       crearClienteUseCase;
    private final ActualizarClienteUseCase  actualizarClienteUseCase;
    private final DesactivarClienteUseCase  desactivarClienteUseCase;
    private final ListarClientesUseCase     listarClientesUseCase;
    private final BuscarClientePorIdUseCase buscarClientePorIdUseCase;

    /**
     * Inyección por constructor de todos los casos de uso requeridos por este adaptador.
     */
    public ClienteController(CrearClienteUseCase      crearClienteUseCase,
                             ActualizarClienteUseCase actualizarClienteUseCase,
                             DesactivarClienteUseCase desactivarClienteUseCase,
                             ListarClientesUseCase    listarClientesUseCase,
                             BuscarClientePorIdUseCase buscarClientePorIdUseCase) {
        this.crearClienteUseCase       = crearClienteUseCase;
        this.actualizarClienteUseCase  = actualizarClienteUseCase;
        this.desactivarClienteUseCase  = desactivarClienteUseCase;
        this.listarClientesUseCase     = listarClientesUseCase;
        this.buscarClientePorIdUseCase = buscarClientePorIdUseCase;
    }

    // =========================================================================
    // GET /api/clientes — Listar todos los clientes
    // =========================================================================

    /**
     * Retorna el listado completo de clientes registrados.
     *
     * @return 200 OK con la lista de {@link ClienteDomain}.
     */
    @GetMapping
    public ResponseEntity<List<ClienteDomain>> listarTodos() {
        return ResponseEntity.ok(listarClientesUseCase.ejecutar());
    }

    // =========================================================================
    // GET /api/clientes/{id} — Buscar cliente por ID
    // =========================================================================

    /**
     * Busca y retorna un cliente por su ID.
     *
     * @param id Identificador único del cliente.
     * @return 200 OK con el {@link ClienteDomain} encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarClientePorIdUseCase.ejecutar(id));
    }

    // =========================================================================
    // POST /api/clientes — Registrar un nuevo cliente
    // =========================================================================

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * @param dto Datos del cliente recibidos desde el Frontend (React).
     * @return 201 CREATED con el {@link ClienteDomain} persistido.
     */
    @PostMapping
    public ResponseEntity<ClienteDomain> crear(@Valid @RequestBody ClienteRequestDto dto) {
        CrearClienteCommand command = new CrearClienteCommand(
                dto.getNombre(),
                dto.getRucDni(),
                dto.getTelefono(),
                dto.getEmail(),
                dto.getDireccion()
        );

        ClienteDomain nuevoCliente = crearClienteUseCase.ejecutar(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    // =========================================================================
    // PUT /api/clientes/{id} — Actualizar un cliente existente
    // =========================================================================

    /**
     * Actualiza los datos de un cliente existente.
     *
     * <p>El RUC/DNI es un campo de identidad inmutable: no puede modificarse
     * una vez que el cliente ha sido creado.</p>
     *
     * @param id  Identificador único del cliente a actualizar (path variable).
     * @param dto Nuevos datos del cliente. El campo {@code estado} es opcional;
     *            si no se envía ({@code null}), el estado actual no cambia.
     * @return 200 OK con el {@link ClienteDomain} actualizado.
     *         404 NOT_FOUND si no existe un cliente con el ID indicado
     *         (manejado por {@link ClienteRestExceptionHandler}).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDomain> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteRequestDto dto) {

        ActualizarClienteCommand command = new ActualizarClienteCommand(
                dto.getNombre(),
                dto.getTelefono(),
                dto.getEmail(),
                dto.getDireccion(),
                dto.getEstado()
        );

        ClienteDomain clienteActualizado = actualizarClienteUseCase.ejecutar(id, command);
        return ResponseEntity.ok(clienteActualizado);
    }

    // =========================================================================
    // DELETE /api/clientes/{id} — Desactivar (baja lógica) un cliente
    // =========================================================================

    /**
     * Desactiva lógicamente un cliente (soft delete).
     *
     * <p>No elimina el registro de la base de datos para preservar la integridad
     * referencial con las ventas históricas. El cliente queda con
     * {@code estado = false} y es excluido de los listados operativos.</p>
     *
     * @param id Identificador único del cliente a desactivar (path variable).
     * @return 200 OK con un mensaje de confirmación y el objeto desactivado.
     *         404 NOT_FOUND si no existe un cliente con el ID indicado
     *         (manejado por {@link ClienteRestExceptionHandler}).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> desactivar(@PathVariable Long id) {
        ClienteDomain clienteDesactivado = desactivarClienteUseCase.ejecutar(id);

        return ResponseEntity.ok(Map.of(
                "mensaje", "El cliente ha sido desactivado exitosamente.",
                "clienteId", clienteDesactivado.getId(),
                "estado", clienteDesactivado.getEstado()
        ));
    }
}
