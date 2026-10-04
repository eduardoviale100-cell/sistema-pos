package com.example.pos.domain.model;

/**
 * Roles disponibles en el sistema POS.
 *
 * <ul>
 *   <li>{@code ADMIN}    – Acceso total al sistema.</li>
 *   <li>{@code VENDEDOR} – Acceso a ventas y consultas básicas.</li>
 *   <li>{@code CAJERO}   – Acceso a caja y ventas.</li>
 * </ul>
 */
public enum RolUsuario {
    ADMIN,
    VENDEDOR,
    CAJERO;

    /**
     * Parsea un String a RolUsuario de forma segura (case-insensitive).
     *
     * @param valor Cadena con el nombre del rol.
     * @return {@link RolUsuario} correspondiente.
     * @throws IllegalArgumentException si el valor no es un rol válido.
     */
    public static RolUsuario fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return VENDEDOR; // Rol por defecto
        }
        try {
            return RolUsuario.valueOf(valor.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Rol inválido: '" + valor + "'. Los roles permitidos son: ADMIN, VENDEDOR, CAJERO.");
        }
    }
}
