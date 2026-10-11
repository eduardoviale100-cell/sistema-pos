package com.example.pos.application.usecase;

import com.example.pos.domain.model.InventarioDomain;
import com.example.pos.domain.model.ProductoDomain;
import com.example.pos.domain.repository.InventarioRepositoryPort;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Caso de Uso: Listar el inventario completo o consultar alertas de productos con bajo stock.
 *
 * <p>Al listar todos los inventarios, sincroniza automáticamente creando registros
 * de inventario (con stock 0) para cualquier producto del catálogo que aún no tenga
 * uno asignado. De esta forma, todos los productos son visibles y gestionables
 * desde la vista de Control de Inventario.</p>
 */
@Service
public class ListarInventarioUseCase {

    private final InventarioRepositoryPort inventarioRepositoryPort;
    private final ProductoRepositoryPort productoRepositoryPort;

    public ListarInventarioUseCase(InventarioRepositoryPort inventarioRepositoryPort,
                                   ProductoRepositoryPort productoRepositoryPort) {
        this.inventarioRepositoryPort = inventarioRepositoryPort;
        this.productoRepositoryPort = productoRepositoryPort;
    }

    /**
     * Retorna el inventario completo. Si existen productos sin registro de inventario,
     * los crea automáticamente con stock 0 y stock mínimo 5.
     */
    @Transactional
    public List<InventarioDomain> listarTodos() {
        List<InventarioDomain> inventarios = inventarioRepositoryPort.listarTodos();

        // Recopilar los IDs de productos que ya tienen inventario
        Set<Long> productosConInventario = inventarios.stream()
                .map(InventarioDomain::getProductoId)
                .collect(Collectors.toSet());

        // Obtener todos los productos del catálogo
        List<ProductoDomain> todosLosProductos = productoRepositoryPort.listarTodos();

        // Crear registro de inventario para cada producto que no tenga uno
        boolean seCreoAlguno = false;
        for (ProductoDomain producto : todosLosProductos) {
            if (!productosConInventario.contains(producto.getId())) {
                InventarioDomain nuevoInv = new InventarioDomain(
                        producto.getId(),
                        producto.getNombre(),
                        producto.getCodigoBarras(),
                        0,  // stockActual = 0
                        5   // stockMinimo = 5 por defecto
                );
                inventarioRepositoryPort.guardar(nuevoInv);
                seCreoAlguno = true;
            }
        }

        // Si se crearon nuevos registros, volver a consultar para incluirlos con todas sus relaciones
        if (seCreoAlguno) {
            inventarios = inventarioRepositoryPort.listarTodos();
        }

        return inventarios;
    }

    @Transactional(readOnly = true)
    public List<InventarioDomain> listarBajoStock() {
        return inventarioRepositoryPort.listarBajoStock();
    }
}
