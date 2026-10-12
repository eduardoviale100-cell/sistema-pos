package com.example.pos.infrastructure.input.rest.cliente;


import com.example.pos.domain.exception.ClienteNoEncontradoException;
import com.example.pos.domain.exception.ClienteDuplicadoException;
import com.example.pos.application.usecase.cliente.ActualizarClienteCommand;
import com.example.pos.application.usecase.cliente.ActualizarClienteUseCase;
import com.example.pos.application.usecase.cliente.BuscarClientePorIdUseCase;
import com.example.pos.application.usecase.cliente.CrearClienteCommand;
import com.example.pos.application.usecase.cliente.CrearClienteUseCase;
import com.example.pos.application.usecase.cliente.DesactivarClienteUseCase;
import com.example.pos.application.usecase.cliente.ListarClientesUseCase;
import com.example.pos.domain.model.cliente.ClienteDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Adaptador de Entrada (Driving/Primary Adapter) REST para el mÃ³dulo de Cliente.
 *
 * <p>Recibe solicitudes HTTP externas, valida la carga Ãºtil mediante Bean Validation
 * y delega la ejecuciÃ³n a los casos de uso correspondientes de la capa de aplicaciÃ³n.
 * El manejo de excepciones de dominio ({@code ClienteNoEncontradoException},
 * {@code ClienteDuplicadoException}, etc.) es responsabilidad del
 * {@link ClienteRestExceptionHandler} global.</p>
 *
 * <p>Endpoints disponibles:
 * <ul>
 *   <li>GET    /api/clientes        â†’ Listar todos los clientes</li>
 *   <li>GET    /api/clientes/{id}   â†’ Buscar cliente por ID</li>
 *   <li>POST   /api/clientes        â†’ Registrar nuevo cliente</li>
 *   <li>PUT    /api/clientes/{id}   â†’ Actualizar cliente existente</li>
 *   <li>DELETE /api/clientes/{id}   â†’ Desactivar (baja lÃ³gica) cliente</li>
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
     * InyecciÃ³n por constructor de todos los casos de uso requeridos por este adaptador.
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
    // GET /api/clientes â€” Listar todos los clientes
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
    // GET /api/clientes/{id} â€” Buscar cliente por ID
    // =========================================================================

    /**
     * Busca y retorna un cliente por su ID.
     *
     * @param id Identificador Ãºnico del cliente.
     * @return 200 OK con el {@link ClienteDomain} encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarClientePorIdUseCase.ejecutar(id));
    }

    // =========================================================================
    // POST /api/clientes â€” Registrar un nuevo cliente
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
    // PUT /api/clientes/{id} â€” Actualizar un cliente existente
    // =========================================================================

    /**
     * Actualiza los datos de un cliente existente.
     *
     * <p>El RUC/DNI es un campo de identidad inmutable: no puede modificarse
     * una vez que el cliente ha sido creado.</p>
     *
     * @param id  Identificador Ãºnico del cliente a actualizar (path variable).
     * @param dto Nuevos datos del cliente. El campo {@code estado} es opcional;
     *            si no se envÃ­a ({@code null}), el estado actual no cambia.
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
    // DELETE /api/clientes/{id} â€” Desactivar (baja lÃ³gica) un cliente
    // =========================================================================

    /**
     * Desactiva lÃ³gicamente un cliente (soft delete).
     *
     * <p>No elimina el registro de la base de datos para preservar la integridad
     * referencial con las ventas histÃ³ricas. El cliente queda con
     * {@code estado = false} y es excluido de los listados operativos.</p>
     *
     * @param id Identificador Ãºnico del cliente a desactivar (path variable).
     * @return 200 OK con un mensaje de confirmaciÃ³n y el objeto desactivado.
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

