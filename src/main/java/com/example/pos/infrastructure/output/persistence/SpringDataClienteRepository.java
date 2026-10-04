package com.example.pos.infrastructure.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad ClienteEntity.
 * Provee las operaciones CRUD y consultas derivadas de Spring Data.
 */
@Repository
public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, Long> {

    /**
     * Busca una entidad de cliente por su documento o RUC.
     *
     * @param rucDni Número de documento a buscar.
     * @return Optional con la entidad si existe.
     */
    Optional<ClienteEntity> findByRucDni(String rucDni);

    /**
     * Verifica la existencia de un cliente por su RUC o DNI.
     *
     * @param rucDni Número de documento a verificar.
     * @return true si ya existe en la base de datos, false de lo contrario.
     */
    boolean existsByRucDni(String rucDni);
}
