package com.example.pos.infrastructure.output.security;

import com.example.pos.domain.repository.PasswordPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador de seguridad para el puerto PasswordPort.
 * Utiliza BCryptPasswordEncoder de Spring Security para el hashing y verificación
 * de contraseñas.
 */
@Component
public class BcryptPasswordAdapter implements PasswordPort {

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordAdapter() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public BcryptPasswordAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encriptar(String passwordPlano) {
        if (passwordPlano == null) {
            throw new IllegalArgumentException("La contraseña en texto plano no puede ser nula.");
        }
        return passwordEncoder.encode(passwordPlano);
    }

    @Override
    public boolean verificar(String passwordPlano, String hash) {
        if (passwordPlano == null || hash == null) {
            return false;
        }
        // Soporte retrocompatible para usuarios creados durante etapas previas
        if (hash.startsWith("{bcrypt}hashed_")) {
            return hash.equals("{bcrypt}hashed_" + passwordPlano);
        }
        return passwordEncoder.matches(passwordPlano, hash);
    }
}
