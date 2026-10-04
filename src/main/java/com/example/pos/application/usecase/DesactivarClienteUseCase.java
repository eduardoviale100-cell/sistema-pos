package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ClienteNoEncontradoException;
import com.example.pos.domain.model.ClienteDomain;
import com.example.pos.domain.repository.ClienteRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Desactivar (eliminación lógica) un cliente del sistema POS.
 *
 * <p>Adopta el patrón de <strong>baja lógica</strong> (soft delete) en lugar de
 * eliminación física, preservando la integridad referencial con ventas históricas.
 * El cliente pasa al estado {@code activo = false} y queda excluido de las
 * consultas operativas sin perder su trazabilidad.</p>
 *
 * <p>Responsabilidades:
 * <ol>
 *   <li>Buscar el cliente por ID a través del puerto de salida.</li>
 *   <li>Validar su existencia lanzando {@link ClienteNoEncontradoException} si no existe.</li>
 *   <li>Invocar el método {@code desactivar()} del modelo de dominio.</li>
 *   <li>Persistir el cambio de estado mediante el puerto de salida.</li>
 * </ol>
 * </p>
 */
@Service
public class DesactivarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    /**
     * Inyección por constructor del puerto de salida (Driven Port).
     */
    public DesactivarClienteUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    /**
     * Ejecuta la desactivación lógica de un cliente por su identificador.
     *
     * @param id Identificador único del cliente a desactivar.
     * @return {@link ClienteDomain} con el estado actualizado a {@code false}.
     * @throws ClienteNoEncontradoException si no existe un cliente con el ID proporcionado.
     * @throws IllegalArgumentException     si el ID proporcionado es nulo.
     */
    @Transactional
    public ClienteDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo.");
        }

        // 1. Recuperar el cliente; lanzar excepción de dominio si no existe
        ClienteDomain cliente = clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> ClienteNoEncontradoException.porId(id));

        // 2. Aplicar la baja lógica a través del método de dominio
        cliente.desactivar();

        // 3. Persistir el estado actualizado y retornar
        return clienteRepositoryPort.guardar(cliente);
    }
}
