package com.example.pos.infrastructure.output.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Filtro de autenticación para interceptar peticiones y validar el token JWT.
 *
 * <p>Mejoras aplicadas:
 * <ul>
 *   <li>Parseo único del token (evita doble llamada a extraerTodosLosClaims)</li>
 *   <li>Respuesta 401 explícita si el header Authorization tiene un Bearer inválido</li>
 *   <li>Logging de diagnóstico para facilitar depuración</li>
 * </ul>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Sin header Authorization → continuar sin autenticación (Spring Security decidirá si permitir)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        boolean tokenValido = false;

        try {
            // Parseo ÚNICO del token: extraerClaimsSeguro valida firma, expiración y retorna Optional
            Optional<Claims> claimsOpt = jwtService.extraerClaimsSeguro(jwt);

            if (claimsOpt.isPresent()) {
                Claims claims = claimsOpt.get();
                String username = claims.getSubject();

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String rol = claims.get("rol", String.class);
                    List<SimpleGrantedAuthority> authorities = (rol != null)
                            ? List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()))
                            : Collections.emptyList();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                tokenValido = true;
            } else {
                // Token presente pero inválido/expirado → responder 401 directamente
                log.warn("Token JWT inválido o expirado para: {} {}", request.getMethod(), request.getRequestURI());
                SecurityContextHolder.clearContext();
                enviarError401(response, "Token JWT invalido o expirado.");
                return;
            }
        } catch (Throwable e) {
            // Blindaje total: cualquier fallo o excepción en la validación de seguridad retorna 401 limpio
            log.error("Error inesperado validando JWT para {} {}: {}",
                    request.getMethod(), request.getRequestURI(), e.getMessage());
            SecurityContextHolder.clearContext();
            enviarError401(response, "Error al procesar el token de autenticacion.");
            return;
        }

        if (tokenValido) {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Envía una respuesta HTTP 401 con cuerpo JSON estructurado.
     * Esto garantiza que el cliente reciba un error limpio en lugar de una página de error de Spring.
     */
    private void enviarError401(HttpServletResponse response, String mensaje) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                "{\"error\":\"No autorizado\",\"mensaje\":\"" + mensaje + "\",\"status\":401}"
        );
    }
}
