package com.example.pos.domain.repository;

import com.example.pos.domain.model.ClienteDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven Port / Secondary Port) para la persistencia de Cliente.
 * Define el contrato que el adaptador de persistencia (SPI) debe implementar.
 * Esta interfaz pertenece estrictamente al dominio y está libre de dependencias de infraestructura.
 */
public interface ClienteRepositoryPort {

    /**
     * Busca un cliente por su identificador único.
     *
     * @param id Identificador único del cliente.
     * @return Optional con el ClienteDomain si existe, o vacío si no se encuentra.
     */
    Optional<ClienteDomain> buscarPorId(Long id);

    /**
     * Busca un cliente por su número de RUC o DNI (clave de negocio).
     *
     * @param rucDni Documento de identidad o RUC.
     * @return Optional con el ClienteDomain si existe, o vacío si no se encuentra.
     */
    Optional<ClienteDomain> buscarPorRucDni(String rucDni);

    /**
     * Persiste o actualiza un cliente en el almacenamiento.
     *
     * @param cliente Modelo de dominio a guardar o actualizar.
     * @return ClienteDomain persistido con su ID asignado/actualizado.
     */
    ClienteDomain guardar(ClienteDomain cliente);

    /**
     * Obtiene el listado completo de clientes registrados.
     *
     * @return Lista de ClienteDomain.
     */
    List<ClienteDomain> listarTodos();

    /**
     * Elimina físicamente o por identificador a un cliente del sistema.
     *
     * @param id Identificador único del cliente a eliminar.
     */
    void eliminar(Long id);

    /**
     * Verifica la existencia de un cliente por su documento de identidad.
     * Útil para validar duplicidad en los casos de uso antes de persistir.
     *
     * @param rucDni Documento a verificar.
     * @return true si ya existe un cliente con dicho documento, false en caso contrario.
     */
    boolean existePorRucDni(String rucDni);
}
