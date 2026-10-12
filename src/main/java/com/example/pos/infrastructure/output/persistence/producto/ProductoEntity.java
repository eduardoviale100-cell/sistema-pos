package com.example.pos.infrastructure.output.persistence.producto;


import com.example.pos.infrastructure.output.persistence.categoria.CategoriaEntity;
import com.example.pos.infrastructure.output.persistence.marca.MarcaEntity;
import com.example.pos.infrastructure.output.persistence.proveedor.ProveedorEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Entidad JPA para la persistencia de productos en la base de datos relacional.
 *
 * <p>Representa la tabla fÃ­sica {@code productos} en la capa de Infraestructura.
 * Es un modelo de datos plano (sin lÃ³gica de negocio) cuya responsabilidad exclusiva
 * es el mapeo objeto-relacional con JPA.</p>
 *
 * <p>Campos mapeados a la tabla {@code productos}:
 * <ul>
 *   <li>{@code id}          â†’ SERIAL PRIMARY KEY</li>
 *   <li>{@code nombre}      â†’ VARCHAR(150) NOT NULL</li>
 *   <li>{@code codigoBarras}â†’ VARCHAR(50)  UNIQUE  (cÃ³digo de barras o cÃ³digo interno)</li>
 *   <li>{@code descripcion} â†’ TEXT                 (descripciÃ³n ampliada, nullable)</li>
 *   <li>{@code precioVenta} â†’ NUMERIC(10,2) NOT NULL CHECK (>= 0)</li>
 *   <li>{@code stock}       â†’ INT NOT NULL DEFAULT 0 (gestionado en el dominio)</li>
 *   <li>{@code estado}      â†’ BOOLEAN NOT NULL DEFAULT TRUE (baja lÃ³gica)</li>
 * </ul>
 * </p>
 *
 * <p><strong>Nota de diseÃ±o:</strong> Las columnas {@code categoria_id}, {@code marca_id}
 * y {@code proveedor_id} existen en la BD pero se gestionarÃ¡n cuando se implementen
 * sus respectivos mÃ³dulos hexagonales (CategorÃ­a, Marca, Proveedor).</p>
 */
@Entity
@Table(name = "productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "codigo_barras", unique = true, length = 50)
    private String codigoBarras;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "marca_id")
    private Long marcaId;

    @Column(name = "proveedor_id")
    private Long proveedorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", insertable = false, updatable = false)
    private CategoriaEntity categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id", insertable = false, updatable = false)
    private MarcaEntity marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", insertable = false, updatable = false)
    private ProveedorEntity proveedor;

    /**
     * Stock actual del producto. Se gestiona directamente en este mÃ³dulo para
     * operaciones de POS. La tabla {@code inventario} puede sincronizarse
     * en la capa de servicio de inventario.
     */
    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // =========================================================================
    // Constructores
    // =========================================================================

    public ProductoEntity() {
    }

    /**
     * Constructor completo utilizado por el adaptador de persistencia para
     * mapear desde el modelo de dominio hacia la entidad JPA.
     */
    public ProductoEntity(Long id, String nombre, String codigoBarras, String descripcion,
                          BigDecimal precioCompra, BigDecimal precioVenta,
                          Long categoriaId, Long marcaId, Long proveedorId,
                          Integer stock, Boolean estado) {
        this.id           = id;
        this.nombre       = nombre;
        this.codigoBarras = codigoBarras;
        this.descripcion  = descripcion;
        this.precioCompra = precioCompra != null ? precioCompra : BigDecimal.ZERO;
        this.precioVenta  = precioVenta;
        this.categoriaId  = categoriaId;
        this.marcaId      = marcaId;
        this.proveedorId  = proveedorId;
        this.stock        = stock  != null ? stock  : 0;
        this.estado       = estado != null ? estado : true;
    }

    // =========================================================================
    // Getters y Setters
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(Long marcaId) {
        this.marcaId = marcaId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public CategoriaEntity getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaEntity categoria) {
        this.categoria = categoria;
    }

    public MarcaEntity getMarca() {
        return marca;
    }

    public void setMarca(MarcaEntity marca) {
        this.marca = marca;
    }

    public ProveedorEntity getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorEntity proveedor) {
        this.proveedor = proveedor;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}

