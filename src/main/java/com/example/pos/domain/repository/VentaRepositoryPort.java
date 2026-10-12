package com.example.pos.domain.repository;

import com.example.pos.domain.model.venta.VentaDomain;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Driven / Secondary Port) para la persistencia de Ventas.
 * Contrato puro sin dependencias de frameworks ni JPA.
 */
public interface VentaRepositoryPort {

    /**
     * Persiste una venta junto con sus detalles y pagos.
     *
     * @param venta Venta de dominio a guardar.
     * @return {@link VentaDomain} guardada con sus identificadores asignados.
     */
    VentaDomain guardar(VentaDomain venta);

    /**
     * Busca una venta por su identificador único.
     *
     * @param id Identificador de la venta.
     * @return {@link Optional} con el agregado de dominio si existe.
     */
    Optional<VentaDomain> buscarPorId(Long id);

    /**
     * Busca una venta por su código legible de factura (ej. VTA-20261009-00001).
     *
     * @param codigoFactura Código de factura único.
     * @return {@link Optional} con la venta de dominio.
     */
    Optional<VentaDomain> buscarPorCodigoFactura(String codigoFactura);

    /**
     * Obtiene el listado completo de ventas ordenadas cronológicamente descendente.
     *
     * @return Lista de ventas.
     */
    List<VentaDomain> listarTodas();

    /**
     * Obtiene las ventas asociadas a una sesión / turno de caja específico.
     *
     * @param sesionCajaId ID de la sesión de caja.
     * @return Lista de ventas de dicha sesión.
     */
    List<VentaDomain> listarPorSesionCajaId(Long sesionCajaId);

    /**
     * Obtiene las ventas emitidas por un usuario determinado.
     *
     * @param usuarioId ID del usuario cajero.
     * @return Lista de ventas realizadas por el usuario.
     */
    List<VentaDomain> listarPorUsuarioId(Long usuarioId);

    /**
     * Obtiene las ventas filtradas concurrentemente por sesión de caja y cajero.
     *
     * @param sesionCajaId ID de la sesión de caja.
     * @param usuarioId ID del usuario cajero.
     * @return Lista de ventas que coinciden con ambos criterios.
     */
    List<VentaDomain> listarPorSesionCajaIdYUsuarioId(Long sesionCajaId, Long usuarioId);

    /**
     * Retorna el conteo total histórico de ventas para correlativo o métricas.
     *
     * @return Cantidad total de ventas registradas.
     */
    long contarTotalVentas();
}
