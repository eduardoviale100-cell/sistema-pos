package com.example.pos.infrastructure.input.rest.usuario;

import com.example.pos.application.usecase.usuario.*;
import com.example.pos.domain.model.usuario.UsuarioDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST hexagonal para el mÃ³dulo Usuario.
 * Bean name diferenciado para coexistir con el controlador MVC legacy.
 */
@RestController("usuarioControllerHexagonal")
@RequestMapping("/api/v2/usuarios")
public class UsuarioRestController {

    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;

    public UsuarioRestController(
            CrearUsuarioUseCase crearUsuarioUseCase,
            ActualizarUsuarioUseCase actualizarUsuarioUseCase,
            CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase,
            ListarUsuariosUseCase listarUsuariosUseCase,
            BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase,
            EliminarUsuarioUseCase eliminarUsuarioUseCase) {
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.cambiarEstadoUsuarioUseCase = cambiarEstadoUsuarioUseCase;
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.buscarUsuarioPorIdUseCase = buscarUsuarioPorIdUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
    }

    // â”€â”€â”€ GET /api/v2/usuarios â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @GetMapping
    public ResponseEntity<List<UsuarioDomain>> listar(
            @RequestParam(required = false) Boolean soloActivos,
            @RequestParam(required = false) String rol) {
        boolean filterActivos = Boolean.TRUE.equals(soloActivos);
        return ResponseEntity.ok(listarUsuariosUseCase.ejecutar(filterActivos, rol));
    }

    // â”€â”€â”€ GET /api/v2/usuarios/{id} â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDomain> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarUsuarioPorIdUseCase.ejecutar(id));
    }

    // â”€â”€â”€ POST /api/v2/usuarios â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @PostMapping
    public ResponseEntity<UsuarioDomain> crear(@Valid @RequestBody CrearUsuarioRequestDto dto) {
        CrearUsuarioCommand command = new CrearUsuarioCommand(
                dto.getNombre(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getRol()
        );
        UsuarioDomain creado = crearUsuarioUseCase.ejecutar(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // â”€â”€â”€ PUT /api/v2/usuarios/{id} â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDomain> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequestDto dto) {
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand(
                dto.getNombre(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getRol()
        );
        return ResponseEntity.ok(actualizarUsuarioUseCase.ejecutar(id, command));
    }

    // â”€â”€â”€ PATCH /api/v2/usuarios/{id}/estado â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioDomain> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        return ResponseEntity.ok(cambiarEstadoUsuarioUseCase.ejecutar(id, activo));
    }

    // â”€â”€â”€ DELETE /api/v2/usuarios/{id} â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarUsuarioUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }
}

