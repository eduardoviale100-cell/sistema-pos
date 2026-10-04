package com.example.pos.application.usecase;

import com.example.pos.domain.model.ClienteDomain;
import com.example.pos.domain.repository.ClienteRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Listar todos los clientes registrados en el sistema POS.
 *
 * <p>Pertenece a la capa de Aplicación. Es un caso de uso de consulta (query),
 * por lo que se marca como {@code readOnly = true} para optimizar la transacción.</p>
 */
@Service
public class ListarClientesUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public ListarClientesUseCase(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    /**
     * Retorna todos los clientes registrados.
     *
     * @return Lista de {@link ClienteDomain}. Puede estar vacía, nunca es {@code null}.
     */
    @Transactional(readOnly = true)
    public List<ClienteDomain> ejecutar() {
        return clienteRepositoryPort.listarTodos();
    }
}
