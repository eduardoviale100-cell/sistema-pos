package com.example.pos.domain.model.venta;

import com.example.pos.domain.exception.MontoPagoInsuficienteException;
import com.example.pos.domain.exception.VentaYaAnuladaException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Agregado raíz del dominio para Ventas / Facturación POS.
 */
public class VentaDomain {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteDocumento;
    private Long usuarioId;
    private String usuarioNombre;
    private Long sesionCajaId;
    private String codigoFactura;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado; // "COMPLETADA", "ANULADA"
    private String observaciones;
    private List<VentaDetalleDomain> detalles = new ArrayList<>();
    private List<PagoDomain> pagos = new ArrayList<>();

    public VentaDomain() {
        this.fecha = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
        this.estado = "COMPLETADA";
    }

    public static VentaDomain crear(Long sesionCajaId, Long usuarioId, Long clienteId, String observaciones) {
        if (sesionCajaId == null) {
            throw new IllegalArgumentException("La sesión de caja es obligatoria para emitir una venta.");
        }
        if (usuarioId == null) {
            throw new IllegalArgumentException("El usuario cajero es obligatorio para registrar la venta.");
        }

        VentaDomain venta = new VentaDomain();
        venta.setSesionCajaId(sesionCajaId);
        venta.setUsuarioId(usuarioId);
        venta.setClienteId(clienteId);
        venta.setObservaciones(observaciones);
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("COMPLETADA");
        return venta;
    }

    public void agregarDetalle(VentaDetalleDomain detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser nulo.");
        }
        this.detalles.add(detalle);
        recalcularTotal();
    }

    public void agregarPago(PagoDomain pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo.");
        }
        this.pagos.add(pago);
    }

    public void recalcularTotal() {
        this.total = this.detalles.stream()
                .map(VentaDetalleDomain::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalPagado() {
        return this.pagos.stream()
                .map(PagoDomain::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalEfectivo() {
        return this.pagos.stream()
                .filter(PagoDomain::esEfectivo)
                .map(PagoDomain::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void validarPagoSuficiente() {
        BigDecimal totalPagado = calcularTotalPagado();
        if (totalPagado.compareTo(this.total) < 0) {
            throw new MontoPagoInsuficienteException(this.total, totalPagado);
        }
    }

    public void anular() {
        if ("ANULADA".equalsIgnoreCase(this.estado)) {
            throw new VentaYaAnuladaException(this.id);
        }
        this.estado = "ANULADA";
    }

    public boolean isAnulada() {
        return "ANULADA".equalsIgnoreCase(this.estado);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteDocumento() {
        return clienteDocumento;
    }

    public void setClienteDocumento(String clienteDocumento) {
        this.clienteDocumento = clienteDocumento;
    }

    public ClienteSummary getCliente() {
        if (clienteNombre != null || clienteId != null || clienteDocumento != null) {
            return new ClienteSummary(clienteId, clienteNombre, clienteDocumento);
        }
        return null;
    }

    public UsuarioSummary getUsuario() {
        if (usuarioNombre != null || usuarioId != null) {
            return new UsuarioSummary(usuarioId, usuarioNombre);
        }
        return null;
    }

    public SesionCajaSummary getSesionCaja() {
        if (sesionCajaId != null) {
            return new SesionCajaSummary(sesionCajaId);
        }
        return null;
    }

    public record ClienteSummary(Long id, String nombre, String documento) {}
    public record UsuarioSummary(Long id, String nombre) {}
    public record SesionCajaSummary(Long id) {}

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public Long getSesionCajaId() {
        return sesionCajaId;
    }

    public void setSesionCajaId(Long sesionCajaId) {
        this.sesionCajaId = sesionCajaId;
    }

    public String getCodigoFactura() {
        return codigoFactura;
    }

    public void setCodigoFactura(String codigoFactura) {
        this.codigoFactura = codigoFactura;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<VentaDetalleDomain> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<VentaDetalleDomain> detalles) {
        this.detalles = detalles != null ? new ArrayList<>(detalles) : new ArrayList<>();
        recalcularTotal();
    }

    public List<PagoDomain> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    public void setPagos(List<PagoDomain> pagos) {
        this.pagos = pagos != null ? new ArrayList<>(pagos) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VentaDomain that = (VentaDomain) o;
        return Objects.equals(id, that.id) || (codigoFactura != null && Objects.equals(codigoFactura, that.codigoFactura));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codigoFactura);
    }
}
