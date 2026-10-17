package com.example.pos.infrastructure.input.rest.usuario;

import com.example.pos.application.usecase.usuario.*;
import com.example.pos.domain.model.usuario.UsuarioDomain;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST hexagonal para el módulo Usuario.
 * Mapeado a /api/usuarios y /api/v2/usuarios para total compatibilidad y coherencia.
 */
@RestController("usuarioControllerHexagonal")
@RequestMapping({"/api/usuarios", "/api/v2/usuarios"})
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

    // ─── GET /api/usuarios ───────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listar(
            @RequestParam(required = false) Boolean soloActivos,
            @RequestParam(required = false) String rol) {
        boolean filterActivos = Boolean.TRUE.equals(soloActivos);
        List<UsuarioDomain> usuarios = listarUsuariosUseCase.ejecutar(filterActivos, rol);
        List<UsuarioResponseDto> response = usuarios.stream()
                .map(UsuarioResponseDto::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }

    // ─── GET /api/usuarios/{id} ──────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable Long id) {
        UsuarioDomain usuario = buscarUsuarioPorIdUseCase.ejecutar(id);
        return ResponseEntity.ok(UsuarioResponseDto.fromDomain(usuario));
    }

    // ─── POST /api/usuarios ──────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody CrearUsuarioRequestDto dto) {
        CrearUsuarioCommand command = new CrearUsuarioCommand(
                dto.getNombre(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getRol()
        );
        UsuarioDomain creado = crearUsuarioUseCase.ejecutar(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponseDto.fromDomain(creado));
    }

    // ─── PUT /api/usuarios/{id} ──────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequestDto dto) {
        ActualizarUsuarioCommand command = new ActualizarUsuarioCommand(
                dto.getNombre(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getRol()
        );
        UsuarioDomain actualizado = actualizarUsuarioUseCase.ejecutar(id, command);
        return ResponseEntity.ok(UsuarioResponseDto.fromDomain(actualizado));
    }

    // ─── PATCH /api/usuarios/{id}/estado ─────────────────────────────────────
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        UsuarioDomain actualizado = cambiarEstadoUsuarioUseCase.ejecutar(id, activo);
        return ResponseEntity.ok(UsuarioResponseDto.fromDomain(actualizado));
    }

    // ─── DELETE /api/usuarios/{id} ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarUsuarioUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }
}
