package com.example.pos.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad de dominio pura para Producto.
 *
 * <p>Representa el modelo del negocio sin acoplamiento a frameworks externos
 * (Spring, JPA, Jakarta). Encapsula las invariantes y reglas de negocio del
 * producto dentro del sistema POS.</p>
 *
 * <p>Atributos de negocio:
 * <ul>
 *   <li>{@code codigo}     — Código de barras o código interno único (clave de negocio).</li>
 *   <li>{@code nombre}     — Nombre o descripción corta del producto.</li>
 *   <li>{@code descripcion}— Descripción ampliada o detallada (opcional).</li>
 *   <li>{@code precioVenta}— Precio de venta al público (>= 0).</li>
 *   <li>{@code stock}      — Cantidad disponible en inventario (>= 0).</li>
 *   <li>{@code estado}     — Indica si el producto está activo y disponible para la venta.</li>
 * </ul>
 * </p>
 */
public class ProductoDomain {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private Integer stock;
    private Boolean estado;

    /**
     * Constructor vacío requerido para la reconstitución desde la capa de persistencia
     * sin pasar por las validaciones de construcción directa.
     */
    public ProductoDomain() {
        this.estado = true;
        this.stock  = 0;
    }

    /**
     * Constructor para la creación de un nuevo producto en el dominio (sin ID persistido).
     * El estado se inicializa como {@code true} (activo) y el stock en 0.
     *
     * @param codigo      Código único del producto.
     * @param nombre      Nombre del producto.
     * @param descripcion Descripción ampliada (puede ser nula).
     * @param precioVenta Precio de venta (debe ser >= 0).
     */
    public ProductoDomain(String codigo, String nombre, String descripcion, BigDecimal precioVenta) {
        this(null, codigo, nombre, descripcion, precioVenta, 0, true);
    }

    /**
     * Constructor completo para reconstitución desde la capa de persistencia o para
     * pruebas unitarias con control total sobre el estado interno.
     *
     * @param id          Identificador único generado por la BD (puede ser nulo si es nuevo).
     * @param codigo      Código único del producto.
     * @param nombre      Nombre del producto.
     * @param descripcion Descripción ampliada (puede ser nula).
     * @param precioVenta Precio de venta.
     * @param stock       Cantidad en inventario.
     * @param estado      Estado activo/inactivo.
     */
    public ProductoDomain(Long id, String codigo, String nombre, String descripcion,
                          BigDecimal precioVenta, Integer stock, Boolean estado) {
        validarCodigo(codigo);
        validarNombre(nombre);
        validarPrecioVenta(precioVenta);
        validarStock(stock);

        this.id          = id;
        this.codigo      = codigo.trim();
        this.nombre      = nombre.trim();
        this.descripcion = descripcion != null ? descripcion.trim() : null;
        this.precioVenta = precioVenta;
        this.stock       = stock != null ? stock : 0;
        this.estado      = estado != null ? estado : true;
    }

    // =========================================================================
    // Métodos de Lógica de Dominio
    // =========================================================================

    /**
     * Actualiza la información editable del producto respetando los invariantes de negocio.
     * El código (clave de negocio) no puede modificarse una vez creado.
     *
     * @param nuevoNombre      Nombre actualizado (obligatorio).
     * @param nuevaDescripcion Descripción actualizada (opcional).
     * @param nuevoPrecioVenta Precio de venta actualizado (>= 0).
     */
    public void actualizarDatos(String nuevoNombre, String nuevaDescripcion, BigDecimal nuevoPrecioVenta) {
        validarNombre(nuevoNombre);
        validarPrecioVenta(nuevoPrecioVenta);

        this.nombre      = nuevoNombre.trim();
        this.descripcion = nuevaDescripcion != null ? nuevaDescripcion.trim() : null;
        this.precioVenta = nuevoPrecioVenta;
    }

    /**
     * Incrementa el stock del producto. Usado en entradas de inventario o compras.
     *
     * @param cantidad Cantidad a sumar (debe ser > 0).
     * @throws IllegalArgumentException si la cantidad es nula o no positiva.
     */
    public void incrementarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a incrementar debe ser mayor que cero. Valor recibido: " + cantidad);
        }
        this.stock += cantidad;
    }

    /**
     * Decrementa el stock del producto. Usado en ventas o salidas de inventario.
     * Garantiza que el stock nunca sea negativo.
     *
     * @param cantidad Cantidad a restar (debe ser > 0 y <= stock actual).
     * @throws IllegalArgumentException si la cantidad es inválida o excede el stock disponible.
     */
    public void decrementarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a decrementar debe ser mayor que cero. Valor recibido: " + cantidad);
        }
        if (cantidad > this.stock) {
            throw new IllegalArgumentException(
                    "Stock insuficiente para el producto '" + this.nombre + "'. " +
                    "Disponible: " + this.stock + ", Solicitado: " + cantidad);
        }
        this.stock -= cantidad;
    }

    /**
     * Verifica si hay stock disponible para cubrir la cantidad solicitada.
     *
     * @param cantidadSolicitada Cantidad a verificar.
     * @return {@code true} si el stock es suficiente.
     */
    public boolean tieneStockSuficiente(int cantidadSolicitada) {
        return this.stock >= cantidadSolicitada;
    }

    /** Activa el producto (lo hace disponible para la venta). */
    public void activar() {
        this.estado = true;
    }

    /** Desactiva el producto (soft delete / baja lógica). */
    public void desactivar() {
        this.estado = false;
    }

    /** @return {@code true} si el producto está activo. */
    public boolean isActivo() {
        return Boolean.TRUE.equals(this.estado);
    }

    // =========================================================================
    // Validaciones de Invariantes (privadas)
    // =========================================================================

    private void validarCodigo(String valorCodigo) {
        if (valorCodigo == null || valorCodigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del producto es obligatorio.");
        }
        if (valorCodigo.trim().length() > 50) {
            throw new IllegalArgumentException("El código del producto no puede exceder los 50 caracteres.");
        }
    }

    private void validarNombre(String valorNombre) {
        if (valorNombre == null || valorNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo ni estar vacío.");
        }
        if (valorNombre.trim().length() > 150) {
            throw new IllegalArgumentException("El nombre del producto no puede exceder los 150 caracteres.");
        }
    }

    private void validarPrecioVenta(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("El precio de venta del producto es obligatorio.");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio de venta no puede ser negativo. Valor recibido: " + valor);
        }
    }

    private void validarStock(Integer valor) {
        if (valor != null && valor < 0) {
            throw new IllegalArgumentException(
                    "El stock del producto no puede ser negativo. Valor recibido: " + valor);
        }
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        validarCodigo(codigo);
        this.codigo = codigo.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : null;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        validarPrecioVenta(precioVenta);
        this.precioVenta = precioVenta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        validarStock(stock);
        this.stock = stock != null ? stock : 0;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado != null ? estado : true;
    }

    // =========================================================================
    // Identidad y Representación
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoDomain that)) return false;
        // La identidad de negocio está definida por el código único
        return Objects.equals(codigo, that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "ProductoDomain{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precioVenta=" + precioVenta +
                ", stock=" + stock +
                ", estado=" + estado +
                '}';
    }
}
