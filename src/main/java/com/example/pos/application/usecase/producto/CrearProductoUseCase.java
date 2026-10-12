package com.example.pos.application.usecase.producto;

import com.example.pos.domain.exception.ProductoYaExisteException;
import com.example.pos.domain.model.inventario.InventarioDomain;
import com.example.pos.domain.model.producto.ProductoDomain;
import com.example.pos.domain.repository.InventarioRepositoryPort;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de Uso: Registrar un nuevo producto en el sistema POS.
 *
 * <p>Pertenece a la capa de Aplicación. Orquesta las siguientes responsabilidades:
 * <ol>
 *   <li>Validar que el comando no sea nulo.</li>
 *   <li>Verificar la unicidad del código mediante el puerto de salida,
 *       lanzando {@link ProductoYaExisteException} si ya existe.</li>
 *   <li>Instanciar el modelo de dominio puro (que aplica sus propias invariantes internas).</li>
 *   <li>Persistir y retornar el producto guardado mediante el puerto de salida.</li>
 *   <li>Crear automáticamente un registro de inventario inicial (stock 0) para el producto.</li>
 * </ol>
 * </p>
 *
 * <p>Solo usa {@code @Service} y {@code @Transactional} de Spring, que son anotaciones
 * de infraestructura ligeras y ampliamente aceptadas en la capa de aplicación hexagonal.</p>
 */
@Service
public class CrearProductoUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;

    /**
     * Inyección por constructor de los puertos de salida (Driven Ports).
     */
    public CrearProductoUseCase(ProductoRepositoryPort productoRepositoryPort,
                                InventarioRepositoryPort inventarioRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
        this.inventarioRepositoryPort = inventarioRepositoryPort;
    }

    /**
     * Ejecuta el registro de un nuevo producto a partir de un comando.
     *
     * @param command Datos del producto a crear.
     * @return {@link ProductoDomain} persistido con su identificador generado.
     * @throws ProductoYaExisteException si ya existe un producto con el mismo código.
     * @throws IllegalArgumentException  si el comando es nulo o contiene datos inválidos.
     */
    @Transactional
    public ProductoDomain ejecutar(CrearProductoCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de creación de producto no puede ser nulo.");
        }

        String codigoNormalizado = command.codigo();

        // 1. Validar unicidad del código como clave de negocio
        if (productoRepositoryPort.existePorCodigo(codigoNormalizado)) {
            throw ProductoYaExisteException.porCodigo(codigoNormalizado);
        }

        // 2. Instanciar el modelo de dominio puro (aplica validaciones internas de invariantes)
        ProductoDomain nuevoProducto = new ProductoDomain(
                codigoNormalizado,
                command.nombre(),
                command.descripcion(),
                command.precioCompra(),
                command.precioVenta(),
                command.categoriaId(),
                command.marcaId(),
                command.proveedorId()
        );

        // 3. Persistir el producto
        ProductoDomain productoGuardado = productoRepositoryPort.guardar(nuevoProducto);

        // 4. Crear automáticamente el registro de inventario inicial (stock 0, mínimo 5)
        InventarioDomain inventarioInicial = new InventarioDomain(
                productoGuardado.getId(),
                productoGuardado.getNombre(),
                productoGuardado.getCodigoBarras(),
                0,  // stock actual inicial
                5   // stock mínimo por defecto
        );
        inventarioRepositoryPort.guardar(inventarioInicial);

        return productoGuardado;
    }
}
