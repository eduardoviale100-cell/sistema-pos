package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ClienteDuplicadoException;
import com.example.pos.domain.model.ClienteDomain;
import com.example.pos.domain.repository.ClienteRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Registrar un nuevo cliente en el sistema POS.
 * Pertenece a la capa de Aplicación. Orquesta la verificación de invariantes,
 * validación de duplicados y la persistencia a través del puerto secundario.
 */
@Service
public class CrearClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    /**
     * Inyección por constructor del puerto de salida (Driven Port).
     */
    public CrearClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    /**
     * Ejecuta la creación de un nuevo cliente a partir de atributos individuales.
     *
     * @param nombre    Razón social o nombre completo del cliente.
     * @param rucDni    Número de documento único (RUC o DNI).
     * @param telefono  Teléfono o móvil de contacto.
     * @param email     Correo electrónico.
     * @param direccion Dirección física o fiscal.
     * @return ClienteDomain persistido con su identificador generado.
     * @throws ClienteDuplicadoException si ya existe un cliente con el mismo RUC/DNI.
     */
    @Transactional
    public ClienteDomain ejecutar(String nombre, String rucDni, String telefono, String email, String direccion) {
        String docNormalizado = (rucDni != null) ? rucDni.trim() : "";

        // 1. Validar unicidad a través del puerto de dominio
        if (clienteRepositoryPort.existePorRucDni(docNormalizado)) {
            throw ClienteDuplicadoException.porDocumento(docNormalizado);
        }

        // 2. Instanciar el modelo de dominio puro (aplica validaciones internas de negocio)
        ClienteDomain nuevoCliente = new ClienteDomain(nombre, docNormalizado, telefono, email, direccion);

        // 3. Persistir y retornar mediante el puerto
        return clienteRepositoryPort.guardar(nuevoCliente);
    }

    /**
     * Sobrecarga orientada al patrón Command para desacoplar las llamadas de adaptadores primarios (REST/GraphQL).
     */
    @Transactional
    public ClienteDomain ejecutar(CrearClienteCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de creación no puede ser nulo.");
        }
        return ejecutar(
                command.nombre(),
                command.rucDni(),
                command.telefono(),
                command.email(),
                command.direccion()
        );
    }
}

