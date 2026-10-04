package com.example.pos.domain.repository;

/**
 * Puerto de Salida para la encriptación y verificación de contraseñas.
 *
 * <p>Desacopla la capa de aplicación de la implementación concreta de encriptado
 * (BCrypt, Argon2, etc.). La implementación se provee en la capa de infraestructura.</p>
 */
public interface PasswordPort {

    /**
     * Encripta una contraseña en texto plano.
     *
     * @param passwordPlano Contraseña en texto plano.
     * @return Hash de la contraseña.
     */
    String encriptar(String passwordPlano);

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     *
     * @param passwordPlano Contraseña en texto plano.
     * @param hash          Hash almacenado.
     * @return {@code true} si coincide.
     */
    boolean verificar(String passwordPlano, String hash);
}
