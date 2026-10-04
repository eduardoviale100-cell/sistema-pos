package com.example.pos.domain.repository;

import com.example.pos.domain.model.RolUsuario;
import com.example.pos.domain.model.UsuarioDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Usuarios.
 *
 * <p>Define el contrato que la capa de aplicación utiliza para persistir y recuperar
 * usuarios, sin conocer detalles de la implementación (JPA, JDBC, etc.).</p>
 */
public interface UsuarioRepositoryPort {

    /**
     * Persiste o actualiza un usuario.
     *
     * @param usuario Modelo de dominio a guardar.
     * @return {@link UsuarioDomain} persistido con ID asignado.
     */
    UsuarioDomain guardar(UsuarioDomain usuario);

    /**
     * Busca un usuario por su identificador único.
     *
     * @param id Identificador único.
     * @return Optional con el usuario si existe.
     */
    Optional<UsuarioDomain> buscarPorId(Long id);

    /**
     * Busca un usuario por su nombre de usuario único (username).
     *
     * @param username Nombre de usuario.
     * @return Optional con el usuario si existe.
     */
    Optional<UsuarioDomain> buscarPorUsername(String username);

    /**
     * Lista todos los usuarios registrados en el sistema.
     *
     * @return Lista completa de usuarios.
     */
    List<UsuarioDomain> listarTodos();

    /**
     * Lista únicamente los usuarios activos.
     *
     * @return Lista de usuarios con estado activo = true.
     */
    List<UsuarioDomain> listarActivos();

    /**
     * Lista los usuarios que tienen un rol específico.
     *
     * @param rol Rol a filtrar.
     * @return Lista de usuarios con el rol indicado.
     */
    List<UsuarioDomain> listarPorRol(RolUsuario rol);

    /**
     * Verifica si existe un usuario con el username indicado.
     *
     * @param username Nombre de usuario a verificar.
     * @return {@code true} si existe, {@code false} en caso contrario.
     */
    boolean existePorUsername(String username);

    /**
     * Verifica si existe otro usuario con el mismo username, excluyendo al de la id dada.
     * Útil para validar actualización sin auto-colisión.
     *
     * @param username Nombre de usuario a verificar.
     * @param idExcluido ID del usuario a excluir de la búsqueda.
     * @return {@code true} si existe otro usuario con ese username.
     */
    boolean existePorUsernameExcluyendoId(String username, Long idExcluido);

    /**
     * Elimina un usuario por su identificador único.
     *
     * @param id Identificador único.
     */
    void eliminarPorId(Long id);
}
