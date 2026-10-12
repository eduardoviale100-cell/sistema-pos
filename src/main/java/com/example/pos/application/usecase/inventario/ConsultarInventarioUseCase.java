package com.example.pos.application.usecase.inventario;

import com.example.pos.domain.exception.InventarioNoEncontradoException;
import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.model.inventario.InventarioDomain;
import com.example.pos.domain.model.producto.ProductoDomain;
import com.example.pos.domain.repository.InventarioRepositoryPort;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Consultar el estado de inventario y existencias de un producto.
 */
@Service
public class ConsultarInventarioUseCase {

    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;

    public ConsultarInventarioUseCase(
            InventarioRepositoryPort inventarioRepositoryPort,
            ProductoRepositoryPort productoRepositoryPort) {
        this.inventarioRepositoryPort = inventarioRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
    }

    @Transactional(readOnly = true)
    public InventarioDomain ejecutar(Long productoId) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }

        // Si existe el inventario en la tabla, lo retorna
        return inventarioRepositoryPort.buscarPorProductoId(productoId)
                .orElseGet(() -> {
                    // Si aún no se ha generado la fila de inventario, validar que el producto exista
                    ProductoDomain producto = productoRepositoryPort.buscarPorId(productoId)
                            .orElseThrow(() -> ProductoNoEncontradoException.porId(productoId));
                    return new InventarioDomain(
                            producto.getId(),
                            producto.getNombre(),
                            producto.getCodigo(),
                            producto.getStock(),
                            5
                    );
                });
    }
}
