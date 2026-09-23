package com.example.pos.service;

import com.example.pos.dto.CompraDetalleRequestDto;
import com.example.pos.dto.CompraRequestDto;
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
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    public List<Compra> listarCompras() {
        // Usa JOIN FETCH en una sola query para evitar el problema N+1
        // que causaba timeouts contra la BD remota (Neon)
        return compraRepository.findAllWithDetails();
    }

    public Optional<Compra> buscarPorId(Integer id) {
        return compraRepository.findByIdWithDetails(id);
    }

    @Transactional
    public Compra registrarCompra(CompraRequestDto dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + dto.getProveedorId()));

        Usuario usuario = null;
        if (dto.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(dto.getUsuarioId()).orElse(null);
        }

        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setUsuario(usuario);
        compra.setFecha(LocalDateTime.now());
        compra.setObservaciones(dto.getObservaciones());

        BigDecimal totalCalculado = BigDecimal.ZERO;
        List<CompraDetalle> listaDetalles = new ArrayList<>();

        // Guardar cabecera inicial para tener ID de referencia
        Compra compraGuardada = compraRepository.save(compra);

        for (CompraDetalleRequestDto detDto : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detDto.getProductoId()));

            // 1. Aumentar stock en inventario
            Inventario inventario = inventarioRepository.findByProductoId(producto.getId())
                    .orElseGet(() -> {
                        Inventario inv = new Inventario();
                        inv.setProducto(producto);
                        inv.setStockActual(0);
                        inv.setStockMinimo(5);
                        return inv;
                    });

            inventario.setStockActual(inventario.getStockActual() + detDto.getCantidad());
            inventarioRepository.save(inventario);

            // 2. Actualizar último precio de compra en catálogo de producto
            producto.setPrecioCompra(detDto.getPrecioUnitario());
            productoRepository.save(producto);

            // 3. Calcular subtotal
            BigDecimal subtotalLinea = detDto.getPrecioUnitario().multiply(BigDecimal.valueOf(detDto.getCantidad()));
            totalCalculado = totalCalculado.add(subtotalLinea);

            CompraDetalle detalle = new CompraDetalle();
            detalle.setCompra(compraGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(detDto.getCantidad());
            detalle.setPrecioUnitario(detDto.getPrecioUnitario());

            listaDetalles.add(detalle);

            // 4. Registrar en kardex de movimientos de inventario
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(producto);
            movimiento.setTipoMovimiento("ENTRADA");
            movimiento.setCantidad(detDto.getCantidad());
            movimiento.setFecha(LocalDateTime.now());
            movimiento.setUsuario(usuario);
            movimiento.setReferencia("Compra ID: " + compraGuardada.getId());
            movimiento.setObservaciones("Abastecimiento de proveedor: " + proveedor.getNombre());
            movimientoInventarioRepository.save(movimiento);
        }

        compraGuardada.setTotal(totalCalculado);
        compraGuardada.setDetalles(listaDetalles);

        return compraRepository.save(compraGuardada);
    }
}
