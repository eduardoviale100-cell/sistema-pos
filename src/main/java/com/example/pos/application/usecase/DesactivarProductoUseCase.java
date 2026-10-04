package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.model.ProductoDomain;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Desactivar lógicamente un producto (baja lógica / soft delete).
 *
 * <p>Preserva la integridad histórica de ventas y compras al no eliminar físicamente
 * el registro de la base de datos.</p>
 */
@Service
public class DesactivarProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public DesactivarProductoUseCase(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    /**
     * Desactiva un producto marcándolo como inactivo.
     *
     * @param id Identificador único del producto.
     * @return {@link ProductoDomain} con estado inactivo.
     * @throws ProductoNoEncontradoException si el producto no existe.
     * @throws IllegalArgumentException si el ID es nulo.
     */
    @Transactional
    public ProductoDomain ejecutar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }

        ProductoDomain producto = productoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> ProductoNoEncontradoException.porId(id));

        producto.desactivar();
        return productoRepositoryPort.guardar(producto);
    }
}
