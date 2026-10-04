package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.model.ProductoDomain;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Buscar un producto específico por su identificador único.
 *
 * <p>Pertenece a la capa de Aplicación. Es un caso de uso de consulta (query)
 * que lanza {@link ProductoNoEncontradoException} si el ID no corresponde
 * a ningún producto registrado, garantizando una respuesta HTTP 404 semántica.</p>
 *
 * <p>Se marca como {@code readOnly = true} para optimizar la transacción
 * y evitar dirty-checking en JPA.</p>
 */
@Service
public class BuscarProductoPorIdUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    /**
     * Inyección por constructor del puerto de salida (Driven Port).
     */
    public BuscarProductoPorIdUseCase(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    /**
     * Ejecuta la búsqueda de un producto por su identificador único.
     *
     * @param id Identificador único del producto.
     * @return {@link ProductoDomain} encontrado.
     * @throws ProductoNoEncontradoException si no existe un producto con el ID proporcionado.
     * @throws IllegalArgumentException      si el ID es nulo.
     */
    @Transactional(readOnly = true)
    public ProductoDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }

        return productoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> ProductoNoEncontradoException.porId(id));
    }
}
