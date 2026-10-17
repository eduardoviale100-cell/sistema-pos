package com.example.pos.infrastructure.output.security;

import com.example.pos.domain.model.usuario.UsuarioDomain;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Servicio encargado de la generación, extracción y validación de tokens JWT (HS256).
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret:pos-sistema-super-secure-jwt-secret-key-for-hs256-signing-must-be-long-enough}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT para un usuario del dominio.
     */
    public String generarToken(UsuarioDomain usuario) {
        Map<String, Object> extraClaims = new HashMap<>();
        // Forzar almacenamiento como Long para evitar ClassCastException al deserializar
        extraClaims.put("id", usuario.getId() != null ? usuario.getId().longValue() : null);
        extraClaims.put("nombre", usuario.getNombre());
        extraClaims.put("rol", usuario.getRol() != null ? usuario.getRol().name() : "VENDEDOR");

        return generarToken(extraClaims, usuario.getUsername());
    }

    public String generarToken(Map<String, Object> extraClaims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrae todos los claims de forma segura, sin lanzar excepciones.
     * Retorna Optional.empty() si el token es inválido, expirado o malformado.
     */
    public Optional<Claims> extraerClaimsSeguro(String token) {
        try {
            Claims claims = extraerTodosLosClaims(token);
            if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
                log.debug("Token expirado para subject: {}", claims.getSubject());
                return Optional.empty();
            }
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token JWT invalido: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Extrae el ID del usuario desde los claims de forma segura.
     * Maneja la conversión Integer/Long que JJWT puede introducir.
     */
    public Long extraerUsuarioId(Claims claims) {
        Object idObj = claims.get("id");
        if (idObj instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    public boolean esTokenValido(String token, String username) {
        try {
            final String tokenUsername = extraerUsername(token);
            return (tokenUsername != null && tokenUsername.equalsIgnoreCase(username) && !esTokenExpirado(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean esTokenValido(String token) {
        try {
            extraerTodosLosClaims(token);
            return !esTokenExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean esTokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }
}
