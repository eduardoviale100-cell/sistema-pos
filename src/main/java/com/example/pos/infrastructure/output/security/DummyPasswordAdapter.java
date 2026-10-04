package com.example.pos.infrastructure.output.security;

import com.example.pos.domain.repository.PasswordPort;
import org.springframework.stereotype.Component;

/**
 * Adaptador temporal para el puerto de contraseñas.
 * En un sistema real, aquí inyectaríamos PasswordEncoder de Spring Security
 * (típicamente configurado con BCryptPasswordEncoder).
 * Por ahora, para mantener compatibilidad y compilación limpia antes de
 * configurar Spring Security completo, implementamos un mock simple o usamos un dummy hash.
 */
@Component
public class DummyPasswordAdapter implements PasswordPort {

    @Override
    public String encriptar(String passwordPlano) {
        // TODO: Reemplazar con BCrypt.hashpw o passwordEncoder.encode
        // Por motivos de migración inicial, simulamos un hash.
        return "{bcrypt}hashed_" + passwordPlano;
    }

    @Override
    public boolean verificar(String passwordPlano, String hash) {
        // TODO: Reemplazar con BCrypt.checkpw o passwordEncoder.matches
        String expectedHash = "{bcrypt}hashed_" + passwordPlano;
        return expectedHash.equals(hash);
    }
}
