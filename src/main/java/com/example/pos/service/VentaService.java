package com.example.pos.service;

import com.example.pos.dto.PagoRequestDto;
import com.example.pos.dto.VentaDetalleRequestDto;
import com.example.pos.dto.VentaRequestDto;
import com.example.pos.model.*;
import com.example.pos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SesionCajaRepository sesionCajaRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    private void inicializarColecciones(List<Venta> ventas) {
        if (ventas == null || ventas.isEmpty()) return;
        for (Venta v : ventas) {
            if (v.getDetalles() != null) {
                v.getDetalles().forEach(d -> {
                    if (d.getProducto() != null) {
                        d.getProducto().getNombre();
                    }
                });
            }
            if (v.getPagos() != null) {
                v.getPagos().forEach(p -> {
                    if (p.getMetodoPago() != null) {
                        p.getMetodoPago().getNombre();
                    }
                });
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        List<Venta> ventas = ventaRepository.findAllWithDetails();
        inicializarColecciones(ventas);
        return ventas;
    }

    @Transactional(readOnly = true)
    public List<Venta> listarPorUsuario(Integer usuarioId) {
        List<Venta> ventas = ventaRepository.findByUsuarioId(usuarioId);
        inicializarColecciones(ventas);
        return ventas;
    }

    @Transactional(readOnly = true)
    public List<Venta> listarPorSesionCaja(Integer sesionCajaId) {
        List<Venta> ventas = ventaRepository.findBySesionCajaIdWithDetails(sesionCajaId);
        inicializarColecciones(ventas);
        return ventas;
    }

    @Transactional(readOnly = true)
    public List<Venta> listarPorSesionYUsuario(Integer sesionCajaId, Integer usuarioId) {
        List<Venta> ventas = ventaRepository.findBySesionCajaIdAndUsuarioId(sesionCajaId, usuarioId);
        inicializarColecciones(ventas);
        return ventas;
    }

    @Transactional(readOnly = true)
    public Optional<Venta> buscarPorId(Integer id) {
        Optional<Venta> ventaOpt = ventaRepository.findByIdWithDetails(id);
        ventaOpt.ifPresent(v -> {
            if (v.getPagos() != null) {
                v.getPagos().forEach(p -> {
                    if (p.getMetodoPago() != null) {
                        p.getMetodoPago().getNombre();
                    }
                });
            }
        });
        return ventaOpt;
    }

    @Transactional
    public Venta registrarVenta(VentaRequestDto dto) {
        // 1. Validar Sesión de Caja
        SesionCaja sesion = sesionCajaRepository.findById(dto.getSesionCajaId())
                .orElseThrow(() -> new RuntimeException("Sesión de caja no encontrada con ID: " + dto.getSesionCajaId()));

        if (!"ABIERTA".equalsIgnoreCase(sesion.getEstado())) {
            throw new IllegalStateException("La sesión de caja #" + dto.getSesionCajaId() + " no está ABIERTA.");
        }

        // 2. Validar Usuario
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new IllegalStateException("El usuario '" + usuario.getNombre() + "' no se encuentra activo.");
        }

        // 3. Cliente opcional
        Cliente cliente = null;
        if (dto.getClienteId() != null) {
            cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClienteId()));
        }

        // 4. Instanciar Venta
        Venta venta = new Venta();
        venta.setSesionCaja(sesion);
        venta.setUsuario(usuario);
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now());
        venta.setObservaciones(dto.getObservaciones());

        // Generar código de factura legible: VTA-YYYYMMDD-XXXXX
        String fechaStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long countHoy = ventaRepository.count() + 1;
        venta.setCodigoFactura(String.format("VTA-%s-%05d", fechaStr, countHoy));

        BigDecimal totalCalculado = BigDecimal.ZERO;
        List<VentaDetalle> listaDetalles = new ArrayList<>();

        // 5. Procesar líneas de venta y descontar stock del Inventario
        for (VentaDetalleRequestDto detDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detDto.getProductoId()));

            Inventario inventario = inventarioRepository.findByProductoId(producto.getId())
                    .orElseThrow(() -> new RuntimeException("No existe registro de inventario para el producto: " + producto.getNombre()));

            if (inventario.getStockActual() < detDto.getCantidad()) {
                throw new IllegalStateException(String.format(
                        "Stock insuficiente para '%s'. Stock disponible: %d, solicitado: %d",
                        producto.getNombre(), inventario.getStockActual(), detDto.getCantidad()));
            }

            // Descontar inventario
            inventario.setStockActual(inventario.getStockActual() - detDto.getCantidad());
            inventarioRepository.save(inventario);

            BigDecimal precioUnitario = detDto.getPrecioUnitario() != null
                    ? detDto.getPrecioUnitario()
                    : producto.getPrecioVenta();

            BigDecimal subtotalLinea = precioUnitario.multiply(BigDecimal.valueOf(detDto.getCantidad()));
            totalCalculado = totalCalculado.add(subtotalLinea);

            VentaDetalle detalle = new VentaDetalle();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(detDto.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);

            listaDetalles.add(detalle);
        }

        venta.setTotal(totalCalculado);
        venta.setDetalles(listaDetalles);

        // 6. Validar pagos
        BigDecimal totalPagado = BigDecimal.ZERO;
        BigDecimal totalEfectivo = BigDecimal.ZERO;
        List<Pago> listaPagos = new ArrayList<>();

        for (PagoRequestDto pagoDto : dto.getPagos()) {
            MetodoPago metodo = metodoPagoRepository.findById(pagoDto.getMetodoPagoId())
                    .orElseGet(() -> metodoPagoRepository.findByActivoTrue().stream().findFirst()
                            .orElseGet(() -> {
                                MetodoPago m = new MetodoPago();
                                m.setNombre("Efectivo");
                                m.setActivo(true);
                                return metodoPagoRepository.save(m);
                            }));

            if (Boolean.FALSE.equals(metodo.getActivo())) {
                throw new IllegalStateException("El método de pago '" + metodo.getNombre() + "' no está activo.");
            }

            totalPagado = totalPagado.add(pagoDto.getMonto());

            if (metodo.getNombre() != null && metodo.getNombre().toUpperCase().contains("EFECTIVO")) {
                totalEfectivo = totalEfectivo.add(pagoDto.getMonto());
            }

            Pago pago = new Pago();
            pago.setVenta(venta);
            pago.setMetodoPago(metodo);
            pago.setMonto(pagoDto.getMonto());
            pago.setFecha(LocalDateTime.now());

            listaPagos.add(pago);
        }

        if (totalPagado.compareTo(totalCalculado) < 0) {
            throw new IllegalArgumentException(String.format(
                    "Monto de pago insuficiente. Total venta: %s, Total recibido: %s",
                    totalCalculado, totalPagado));
        }

        venta.setPagos(listaPagos);

        // 7. Si hubo pagos en efectivo, registrar ingreso en la sesión de caja
        if (totalEfectivo.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal esperadoActual = sesion.getMontoEsperado() != null
                    ? sesion.getMontoEsperado()
                    : sesion.getMontoInicial();
            sesion.setMontoEsperado(esperadoActual.add(totalEfectivo));
            sesionCajaRepository.save(sesion);
        }

        Venta ventaGuardada = ventaRepository.save(venta);

        // 8. Registrar movimientos de salida en el kardex de inventario
        for (VentaDetalle det : ventaGuardada.getDetalles()) {
            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(det.getProducto());
            mov.setTipoMovimiento("SALIDA");
            mov.setCantidad(det.getCantidad());
            mov.setFecha(LocalDateTime.now());
            mov.setUsuario(usuario);
            mov.setReferencia("Venta ID: " + ventaGuardada.getId());
            mov.setObservaciones("Salida por venta mostrador");
            movimientoInventarioRepository.save(mov);
        }

        return ventaGuardada;
    }
}
