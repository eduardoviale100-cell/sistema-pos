package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.exception.ProveedorNoEncontradoException;
import com.example.pos.domain.model.*;
import com.example.pos.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de Uso: Registrar una nueva compra a proveedor, actualizando precios de costo,
 * existencias físicas en inventario y generando los registros correspondientes de Kardex.
 */
@Service
public class RegistrarCompraUseCase {

    private final CompraRepositoryPort compraRepositoryPort;
    private final ProveedorRepositoryPort proveedorRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final InventarioRepositoryPort inventarioRepositoryPort;

    public RegistrarCompraUseCase(
            CompraRepositoryPort compraRepositoryPort,
            ProveedorRepositoryPort proveedorRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort,
            ProductoRepositoryPort productoRepositoryPort,
            InventarioRepositoryPort inventarioRepositoryPort) {
        this.compraRepositoryPort = compraRepositoryPort;
        this.proveedorRepositoryPort = proveedorRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.inventarioRepositoryPort = inventarioRepositoryPort;
    }

    @Transactional
    public CompraDomain ejecutar(RegistrarCompraCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de compra no puede ser nulo.");
        }
        if (command.detalles() == null || command.detalles().isEmpty()) {
            throw new IllegalArgumentException("La compra debe incluir al menos un producto.");
        }

        // 1. Validar Proveedor
        ProveedorDomain proveedor = proveedorRepositoryPort.buscarPorId(command.proveedorId())
                .orElseThrow(() -> ProveedorNoEncontradoException.porId(command.proveedorId()));

        // 2. Resolver Usuario si fue especificado
        String usuarioNombre = null;
        if (command.usuarioId() != null) {
            usuarioNombre = usuarioRepositoryPort.buscarPorId(command.usuarioId())
                    .map(UsuarioDomain::getNombre)
                    .orElse(null);
        }

        // 3. Crear agregación inicial de Compra
        CompraDomain compra = CompraDomain.crear(
                command.proveedorId(),
                command.usuarioId(),
                command.observaciones()
        );
        compra.setProveedorNombre(proveedor.getNombre());
        compra.setUsuarioNombre(usuarioNombre);

        // 4. Procesar líneas de abastecimiento
        for (RegistrarCompraCommand.DetalleCompraCommand detCommand : command.detalles()) {
            ProductoDomain producto = productoRepositoryPort.buscarPorId(detCommand.productoId())
                    .orElseThrow(() -> ProductoNoEncontradoException.porId(detCommand.productoId()));

            if (detCommand.cantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad comprada debe ser mayor a cero para el producto: " + producto.getNombre());
            }
            if (detCommand.precioUnitario() == null || detCommand.precioUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
            }

            // Aumentar stock del producto
            producto.incrementarStock(detCommand.cantidad());
            productoRepositoryPort.guardar(producto);

            // Aumentar stock en el registro de Inventario
            InventarioDomain inventario = inventarioRepositoryPort.buscarPorProductoId(producto.getId())
                    .orElseGet(() -> new InventarioDomain(
                            producto.getId(),
                            producto.getNombre(),
                            producto.getCodigo(),
                            0,
                            5
                    ));
            inventario.aumentarStock(detCommand.cantidad());
            inventarioRepositoryPort.guardar(inventario);

            // Agregar detalle a la compra
            CompraDetalleDomain detalle = new CompraDetalleDomain(
                    producto.getId(),
                    producto.getNombre(),
                    detCommand.cantidad(),
                    detCommand.precioUnitario()
            );
            compra.agregarDetalle(detalle);

            // Registrar movimiento de auditoría Kardex
            MovimientoInventarioDomain kardex = new MovimientoInventarioDomain(
                    null,
                    producto.getId(),
                    producto.getNombre(),
                    "ENTRADA",
                    detCommand.cantidad(),
                    LocalDateTime.now(),
                    command.usuarioId(),
                    usuarioNombre,
                    "Compra de mercadería",
                    "Abastecimiento de proveedor: " + proveedor.getNombre()
            );
            inventarioRepositoryPort.registrarMovimiento(kardex);
        }

        // 5. Persistir compra completa
        return compraRepositoryPort.guardar(compra);
    }
}
