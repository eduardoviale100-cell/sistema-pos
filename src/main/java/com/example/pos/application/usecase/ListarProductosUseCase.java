package com.example.pos.application.usecase;

import com.example.pos.domain.model.ProductoDomain;
import com.example.pos.domain.repository.ProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Obtener el listado completo de productos registrados en el sistema POS.
 *
 * <p>Pertenece a la capa de Aplicación. Es un caso de uso de consulta (query),
 * por lo que se marca como {@code readOnly = true} para optimizar la transacción
 * y evitar dirty-checking innecesario en JPA.</p>
 *
 * <p>En una evolución futura puede extenderse para soportar paginación,
 * filtros por categoría, marca, estado, o rango de precios.</p>
 */
@Service
public class ListarProductosUseCase {

    private final ProductoRepositoryPort productoRepositoryPort;

    /**
     * Inyección por constructor del puerto de salida (Driven Port).
     */
    public ListarProductosUseCase(ProductoRepositoryPort productoRepositoryPort) {
        this.productoRepositoryPort = productoRepositoryPort;
    }

    /**
     * Retorna todos los productos registrados en el sistema.
     *
     * @return Lista de {@link ProductoDomain}. Puede estar vacía, nunca es {@code null}.
     */
    @Transactional(readOnly = true)
    public List<ProductoDomain> ejecutar() {
        return productoRepositoryPort.listarTodos();
    }
}
