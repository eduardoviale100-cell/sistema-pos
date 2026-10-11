package com.example.pos.application.usecase;

import com.example.pos.domain.exception.*;
import com.example.pos.domain.model.*;
import com.example.pos.domain.repository.*;
import com.example.pos.model.Inventario;
import com.example.pos.model.MovimientoInventario;
import com.example.pos.model.Producto;
import com.example.pos.model.Usuario;
import com.example.pos.repository.InventarioRepository;
import com.example.pos.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Caso de Uso: Registrar una venta en el punto de cobro (POS).
 * Orquesta la validación de sesión de caja, cajero, cliente, inventario, pagos y kardex.
 */
@Service
public class RegistrarVentaUseCase {

    private final VentaRepositoryPort ventaRepositoryPort;
    private final SesionCajaRepositoryPort sesionCajaRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;
    private final MetodoPagoRepositoryPort metodoPagoRepositoryPort;
    private final MovimientoCajaRepositoryPort movimientoCajaRepositoryPort;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public RegistrarVentaUseCase(
            VentaRepositoryPort ventaRepositoryPort,
            SesionCajaRepositoryPort sesionCajaRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort,
            ClienteRepositoryPort clienteRepositoryPort,
            ProductoRepositoryPort productoRepositoryPort,
            MetodoPagoRepositoryPort metodoPagoRepositoryPort,
            MovimientoCajaRepositoryPort movimientoCajaRepositoryPort,
            InventarioRepository inventarioRepository,
            MovimientoInventarioRepository movimientoInventarioRepository) {
        this.ventaRepositoryPort = ventaRepositoryPort;
        this.sesionCajaRepositoryPort = sesionCajaRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
        this.metodoPagoRepositoryPort = metodoPagoRepositoryPort;
        this.movimientoCajaRepositoryPort = movimientoCajaRepositoryPort;
        this.inventarioRepository = inventarioRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Transactional
    public VentaDomain ejecutar(RegistrarVentaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("El comando de venta no puede ser nulo.");
        }
        if (command.detalles() == null || command.detalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto en el detalle.");
        }
        if (command.pagos() == null || command.pagos().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un método de pago.");
        }

        // 1. Validar Sesión de Caja (debe estar ABIERTA)
        SesionCajaDomain sesion = sesionCajaRepositoryPort.buscarPorId(command.sesionCajaId())
                .orElseThrow(() -> new IllegalArgumentException("Sesión de caja no encontrada con ID: " + command.sesionCajaId()));

        if (!sesion.isAbierta()) {
            throw SesionCajaNoAbiertaException.porSesion(command.sesionCajaId());
        }

        // 2. Validar Usuario (debe estar activo)
        UsuarioDomain usuario = usuarioRepositoryPort.buscarPorId(command.usuarioId())
                .orElseThrow(() -> UsuarioNoEncontradoException.porId(command.usuarioId()));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new IllegalStateException("El usuario '" + usuario.getNombre() + "' no se encuentra activo.");
        }

        // 3. Validar Cliente (si se envió)
        String clienteNombre = null;
        if (command.clienteId() != null) {
            ClienteDomain cliente = clienteRepositoryPort.buscarPorId(command.clienteId())
                    .orElseThrow(() -> ClienteNoEncontradoException.porId(command.clienteId()));
            clienteNombre = cliente.getNombre();
        }

        // 4. Crear instancia inicial de Venta en el Dominio
        VentaDomain venta = VentaDomain.crear(
                command.sesionCajaId(),
                command.usuarioId(),
                command.clienteId(),
                command.observaciones()
        );
        venta.setUsuarioNombre(usuario.getNombre());
        venta.setClienteNombre(clienteNombre);

        // Generar código de factura: VTA-YYYYMMDD-XXXXX
        String fechaStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long correlativoHoy = ventaRepositoryPort.contarTotalVentas() + 1;
        venta.setCodigoFactura(String.format("VTA-%s-%05d", fechaStr, correlativoHoy));

        // 5. Procesar productos y descontar stock
        for (RegistrarVentaCommand.DetalleVentaCommand detCommand : command.detalles()) {
            ProductoDomain producto = productoRepositoryPort.buscarPorId(detCommand.productoId())
                    .orElseThrow(() -> ProductoNoEncontradoException.porId(detCommand.productoId()));

            if (detCommand.cantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero para el producto: " + producto.getNombre());
            }

            // Validar stock disponible
            if (!producto.tieneStockSuficiente(detCommand.cantidad())) {
                throw new StockInsuficienteVentaException(
                        producto.getNombre(),
                        producto.getStock(),
                        detCommand.cantidad()
                );
            }

            // Descontar en el modelo de dominio y persistir
            producto.decrementarStock(detCommand.cantidad());
            productoRepositoryPort.guardar(producto);

            // Sincronizar tabla física 'inventario' legacy si existe
            inventarioRepository.findByProductoId(producto.getId().intValue()).ifPresent(inv -> {
                inv.setStockActual(producto.getStock());
                inventarioRepository.save(inv);
            });

            // Determinar precio de venta
            BigDecimal precioFinal = (detCommand.precioUnitario() != null && detCommand.precioUnitario().compareTo(BigDecimal.ZERO) >= 0)
                    ? detCommand.precioUnitario()
                    : producto.getPrecioVenta();

            VentaDetalleDomain detalle = new VentaDetalleDomain(
                    producto.getId(),
                    producto.getNombre(),
                    detCommand.cantidad(),
                    precioFinal
            );
            venta.agregarDetalle(detalle);
        }

        // 6. Procesar métodos de pago
        for (RegistrarVentaCommand.PagoVentaCommand pagoCommand : command.pagos()) {
            MetodoPagoDomain metodo = metodoPagoRepositoryPort.buscarPorId(pagoCommand.metodoPagoId())
                    .orElseThrow(() -> MetodoPagoNoEncontradoException.porId(pagoCommand.metodoPagoId()));

            if (Boolean.FALSE.equals(metodo.getActivo())) {
                throw new IllegalStateException("El método de pago '" + metodo.getNombre() + "' no se encuentra activo.");
            }

            PagoDomain pago = new PagoDomain(
                    metodo.getId(),
                    metodo.getNombre(),
                    pagoCommand.monto()
            );
            venta.agregarPago(pago);
        }

        // 7. Validar que el pago cubra el total
        venta.validarPagoSuficiente();

        // 8. Si hubo cobros en efectivo, reflejar el ingreso en la sesión de caja y generar movimiento
        BigDecimal totalEfectivo = venta.calcularTotalEfectivo();
        if (totalEfectivo.compareTo(BigDecimal.ZERO) > 0) {
            sesion.registrarIngreso(totalEfectivo);
            sesionCajaRepositoryPort.guardar(sesion);

            MovimientoCajaDomain movCaja = new MovimientoCajaDomain(
                    null,
                    sesion,
                    usuario,
                    "INGRESO",
                    totalEfectivo,
                    LocalDateTime.now(),
                    venta.getCodigoFactura(),
                    "Cobro en efectivo - Venta " + venta.getCodigoFactura()
            );
            movimientoCajaRepositoryPort.guardar(movCaja);
        }

        // 9. Persistir Venta completa con detalles y pagos
        VentaDomain ventaGuardada = ventaRepositoryPort.guardar(venta);

        // 10. Registrar movimiento de auditoría/kardex en movimientos_inventario
        for (VentaDetalleDomain det : ventaGuardada.getDetalles()) {
            MovimientoInventario movInv = new MovimientoInventario();
            Producto prodRef = new Producto();
            prodRef.setId(det.getProductoId().intValue());
            movInv.setProducto(prodRef);

            Usuario usuRef = new Usuario();
            usuRef.setId(usuario.getId().intValue());
            movInv.setUsuario(usuRef);

            movInv.setTipoMovimiento("SALIDA");
            movInv.setCantidad(det.getCantidad());
            movInv.setFecha(LocalDateTime.now());
            movInv.setReferencia("Venta ID: " + ventaGuardada.getId());
            movInv.setObservaciones("Salida por venta mostrador (" + ventaGuardada.getCodigoFactura() + ")");
            movimientoInventarioRepository.save(movInv);
        }

        return ventaGuardada;
    }
}
