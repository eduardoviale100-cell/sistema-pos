package com.example.pos.domain.repository;

import com.example.pos.domain.model.inventario.InventarioDomain;
import com.example.pos.domain.model.inventario.MovimientoInventarioDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Inventario y Kardex.
 */
public interface InventarioRepositoryPort {

    /**
     * Guarda o actualiza el registro de existencias de inventario.
     *
     * @param inventario Modelo de dominio de inventario.
     * @return {@link InventarioDomain} persistido.
     */
    InventarioDomain guardar(InventarioDomain inventario);

    /**
     * Busca el registro de inventario por su ID técnico.
     *
     * @param id ID de inventario.
     * @return {@link Optional} con el inventario si existe.
     */
    Optional<InventarioDomain> buscarPorId(Long id);

    /**
     * Busca las existencias de inventario por el ID del producto asociado.
     *
     * @param productoId ID del producto.
     * @return {@link Optional} con el inventario del producto.
     */
    Optional<InventarioDomain> buscarPorProductoId(Long productoId);

    /**
     * Obtiene el listado completo de inventario con datos del producto.
     *
     * @return Lista de inventarios.
     */
    List<InventarioDomain> listarTodos();

    /**
     * Obtiene los productos con stock igual o inferior al stock mínimo configurado.
     *
     * @return Lista de inventarios en alerta de bajo stock.
     */
    List<InventarioDomain> listarBajoStock();

    /**
     * Registra un nuevo movimiento en el historial de Kardex.
     *
     * @param movimiento Modelo de dominio de movimiento de inventario.
     * @return {@link MovimientoInventarioDomain} guardado con su ID asignado.
     */
    MovimientoInventarioDomain registrarMovimiento(MovimientoInventarioDomain movimiento);

    /**
     * Obtiene los movimientos de Kardex asociados a un producto específico.
     *
     * @param productoId ID del producto.
     * @return Lista cronológica de movimientos.
     */
    List<MovimientoInventarioDomain> listarMovimientosPorProductoId(Long productoId);

    /**
     * Obtiene todos los movimientos de Kardex del sistema.
     *
     * @return Lista completa de movimientos.
     */
    List<MovimientoInventarioDomain> listarTodosLosMovimientos();
}
