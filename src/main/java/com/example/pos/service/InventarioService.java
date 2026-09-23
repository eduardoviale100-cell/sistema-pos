package com.example.pos.service;

import com.example.pos.dto.AjusteStockRequestDto;
import com.example.pos.model.Inventario;
import com.example.pos.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private com.example.pos.repository.MovimientoInventarioRepository movimientoInventarioRepository;

    public List<Inventario> listarTodo() {
        return inventarioRepository.findAll();
    }

    public Optional<Inventario> buscarPorProductoId(Integer productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    public List<Inventario> listarBajoStock() {
        return inventarioRepository.buscarProductosConBajoStock();
    }

    public List<com.example.pos.model.MovimientoInventario> listarMovimientosPorProducto(Integer productoId) {
        return movimientoInventarioRepository.findByProductoId(productoId);
    }

    @Transactional
    public Inventario ajustarStock(Integer productoId, AjusteStockRequestDto dto) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("No se encontró inventario para el producto con ID: " + productoId));

        int cantidad = dto.getCantidad();
        String tipo = dto.getTipo().toUpperCase();

        switch (tipo) {
            case "ENTRADA":
                inventario.setStockActual(inventario.getStockActual() + cantidad);
                break;
            case "SALIDA":
                if (inventario.getStockActual() < cantidad) {
                    throw new IllegalArgumentException(
                            String.format("Stock insuficiente. Stock disponible: %d, cantidad requerida: %d",
                                    inventario.getStockActual(), cantidad));
                }
                inventario.setStockActual(inventario.getStockActual() - cantidad);
                break;
            case "AJUSTE":
                inventario.setStockActual(cantidad);
                break;
            default:
                throw new IllegalArgumentException("Tipo de operación no válida: " + dto.getTipo());
        }

        Inventario guardado = inventarioRepository.save(inventario);

        // Registrar en historial de movimientos
        com.example.pos.model.MovimientoInventario mov = new com.example.pos.model.MovimientoInventario();
        mov.setProducto(inventario.getProducto());
        mov.setTipoMovimiento(tipo);
        mov.setCantidad(cantidad);
        mov.setFecha(java.time.LocalDateTime.now());
        mov.setReferencia("Ajuste Manual");
        mov.setObservaciones(dto.getMotivo());
        movimientoInventarioRepository.save(mov);

        return guardado;
    }
}
