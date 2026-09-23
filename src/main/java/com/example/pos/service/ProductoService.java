package com.example.pos.service;

import com.example.pos.dto.ProductoRequestDto;
import com.example.pos.model.*;
import com.example.pos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public Producto guardarProducto(ProductoRequestDto dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setCodigoBarras(dto.getCodigoBarras());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setPrecioVenta(dto.getPrecioVenta());

        // Mapear relaciones si vienen en el DTO
        if (dto.getCategoriaId() != null) {
            Categoria cat = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(cat);
        }

        if (dto.getMarcaId() != null) {
            Marca marca = marcaRepository.findById(dto.getMarcaId())
                    .orElseThrow(() -> new RuntimeException("Marca no encontrada"));
            producto.setMarca(marca);
        }

        if (dto.getProveedorId() != null) {
            Proveedor prov = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            producto.setProveedor(prov);
        }

        // Guardar producto
        Producto productoGuardado = productoRepository.save(producto);

        // Crear automáticamente su registro inicial en el inventario con stock en 0
        Inventario inventario = new Inventario();
        inventario.setProducto(productoGuardado);
        inventario.setStockActual(0);
        inventario.setStockMinimo(5);
        inventarioRepository.save(inventario);

        return productoGuardado;
    }

    @Transactional
    public Producto actualizarProducto(Integer id, ProductoRequestDto dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        producto.setNombre(dto.getNombre().trim());
        producto.setCodigoBarras(dto.getCodigoBarras() != null ? dto.getCodigoBarras().trim() : null);
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setPrecioVenta(dto.getPrecioVenta());

        if (dto.getCategoriaId() != null) {
            Categoria cat = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getCategoriaId()));
            producto.setCategoria(cat);
        } else {
            producto.setCategoria(null);
        }

        if (dto.getMarcaId() != null) {
            Marca marca = marcaRepository.findById(dto.getMarcaId())
                    .orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + dto.getMarcaId()));
            producto.setMarca(marca);
        } else {
            producto.setMarca(null);
        }

        if (dto.getProveedorId() != null) {
            Proveedor prov = proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + dto.getProveedorId()));
            producto.setProveedor(prov);
        } else {
            producto.setProveedor(null);
        }

        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminarProducto(Integer id) {
        productoRepository.deleteById(id);
    }
}
