package com.example.pos.application.usecase;

import com.example.pos.domain.exception.ProductoNoEncontradoException;
import com.example.pos.domain.exception.TipoMovimientoInventarioInvalidoException;
import com.example.pos.domain.model.InventarioDomain;
import com.example.pos.domain.model.MovimientoInventarioDomain;
import com.example.pos.domain.model.ProductoDomain;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.InventarioRepositoryPort;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Caso de Uso: Registrar ajustes de stock (ENTRADA, SALIDA o AJUSTE) y auditar en Kardex.
 */
@Service
public class AjustarStockInventarioUseCase {

    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public AjustarStockInventarioUseCase(
            InventarioRepositoryPort inventarioRepositoryPort,
            ProductoRepositoryPort productoRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort) {
        this.inventarioRepositoryPort = inventarioRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Transactional
    public InventarioDomain ejecutar(AjustarStockCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de ajuste no puede ser nulo.");
        }
        if (command.productoId() == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }
        if (command.cantidad() == null || command.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (command.tipo() == null || command.tipo().isBlank()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }

        // 1. Obtener producto y validar existencia
        ProductoDomain producto = productoRepositoryPort.buscarPorId(command.productoId())
                .orElseThrow(() -> ProductoNoEncontradoException.porId(command.productoId()));

        // 2. Obtener o inicializar registro de inventario
        InventarioDomain inventario = inventarioRepositoryPort.buscarPorProductoId(command.productoId())
                .orElseGet(() -> new InventarioDomain(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getCodigo(),
                        producto.getStock(),
                        5
                ));

        String tipoNormalizado = command.tipo().trim().toUpperCase();

        // 3. Aplicar ajuste según el tipo de movimiento
        switch (tipoNormalizado) {
            case "ENTRADA" -> {
                inventario.aumentarStock(command.cantidad());
                producto.incrementarStock(command.cantidad());
            }
            case "SALIDA" -> {
                inventario.disminuirStock(command.cantidad());
                producto.decrementarStock(command.cantidad());
            }
            case "AJUSTE" -> {
                inventario.establecerStock(command.cantidad());
                producto.setStock(command.cantidad());
            }
            default -> throw new TipoMovimientoInventarioInvalidoException(command.tipo());
        }

        // 4. Persistir cambios sincronizados en producto e inventario
        productoRepositoryPort.guardar(producto);
        InventarioDomain inventarioGuardado = inventarioRepositoryPort.guardar(inventario);

        // 5. Resolver datos del usuario auditor si fue provisto
        String usuarioNombre = null;
        if (command.usuarioId() != null) {
            usuarioNombre = usuarioRepositoryPort.buscarPorId(command.usuarioId())
                    .map(UsuarioDomain::getNombre)
                    .orElse(null);
        }

        // 6. Registrar movimiento de Kardex
        MovimientoInventarioDomain movimiento = new MovimientoInventarioDomain(
                null,
                producto.getId(),
                producto.getNombre(),
                tipoNormalizado,
                command.cantidad(),
                LocalDateTime.now(),
                command.usuarioId(),
                usuarioNombre,
                "Ajuste Manual",
                command.motivo() != null && !command.motivo().isBlank() ? command.motivo().trim() : "Ajuste de inventario"
        );
        inventarioRepositoryPort.registrarMovimiento(movimiento);

        return inventarioGuardado;
    }
}
