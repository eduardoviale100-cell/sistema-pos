package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.model.ProductoDomain;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Actualizar los datos de un producto existente.
 *
 * <p>Pertenece a la capa de Aplicación. Busca el producto por ID,
 * aplica las modificaciones al modelo de dominio respetando sus invariantes
 * y persiste el resultado a través del puerto de salida.</p>
 */
@Service
public class ActualizarProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    public ActualizarProductoUseCase(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    /**
     * Ejecuta la actualización de un producto.
     *
     * @param id Identificador único del producto.
     * @param command Datos a actualizar.
     * @return {@link ProductoDomain} actualizado.
     * @throws ProductoNoEncontradoException si no existe el producto con el ID especificado.
     * @throws IllegalArgumentException si el ID o comando son nulos.
     */
    @Transactional
    public ProductoDomain ejecutar(Long id, ActualizarProductoCommand command) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }
        if (command == null) {
            throw new IllegalArgumentException("El comando de actualización de producto no puede ser nulo.");
        }

        ProductoDomain producto = productoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> ProductoNoEncontradoException.porId(id));

        producto.actualizarDatos(command.nombre(), command.descripcion(), command.precioVenta());

        if (command.stock() != null) {
            producto.setStock(command.stock());
        }

        if (command.estado() != null) {
            if (command.estado()) {
                producto.activar();
            } else {
                producto.desactivar();
            }
        }

        return productoRepositoryPort.guardar(producto);
    }
}
