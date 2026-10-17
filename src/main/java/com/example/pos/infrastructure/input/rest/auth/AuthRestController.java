package com.example.pos.infrastructure.input.rest.auth;

import com.example.pos.application.usecase.auth.LoginUseCase;
import com.example.pos.domain.model.usuario.UsuarioDomain;
import com.example.pos.infrastructure.output.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el módulo de Autenticación.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final LoginUseCase loginUseCase;
    private final JwtService jwtService;

    public AuthRestController(LoginUseCase loginUseCase, JwtService jwtService) {
        this.loginUseCase = loginUseCase;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint de inicio de sesión: POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        UsuarioDomain usuario = loginUseCase.ejecutar(request.getUsuario(), request.getContrasena());
        String token = jwtService.generarToken(usuario);

        LoginResponseDto.UsuarioInfo info = LoginResponseDto.UsuarioInfo.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .usuario(usuario.getUsername())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : "VENDEDOR")
                .build();

        LoginResponseDto response = LoginResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .usuario(usuario.getUsername())
                .rol(usuario.getRol() != null ? usuario.getRol().name() : "VENDEDOR")
                .usuarioInfo(info)
                .build();

        return ResponseEntity.ok(response);
    }
}
