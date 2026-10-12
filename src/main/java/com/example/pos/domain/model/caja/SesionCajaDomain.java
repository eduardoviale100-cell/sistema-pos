package com.example.pos.domain.model.caja;

import com.example.pos.domain.model.usuario.UsuarioDomain;

import com.example.pos.domain.exception.SaldoInsuficienteCajaException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de dominio puro para una Sesión / Turno de Caja.
 */
public class SesionCajaDomain {

    private Long id;
    private CajaDomain caja;
    private UsuarioDomain usuario;
    private LocalDateTime fechaApertura;
    private BigDecimal montoInicial;
    private LocalDateTime fechaCierre;
    private BigDecimal montoEsperado;
    private BigDecimal montoReal;
    private BigDecimal diferencia;
    private String estado; // "ABIERTA", "CERRADA"

    public SesionCajaDomain() {
        this.estado = "ABIERTA";
        this.montoInicial = BigDecimal.ZERO;
        this.montoEsperado = BigDecimal.ZERO;
    }

    public static SesionCajaDomain iniciar(CajaDomain caja, UsuarioDomain usuario, BigDecimal montoInicial, LocalDateTime fechaApertura) {
        if (caja == null) {
            throw new IllegalArgumentException("La caja es obligatoria para iniciar una sesión.");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario es obligatorio para iniciar una sesión.");
        }
        if (montoInicial == null || montoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto inicial no puede ser negativo.");
        }

        SesionCajaDomain sesion = new SesionCajaDomain();
        sesion.setCaja(caja);
        sesion.setUsuario(usuario);
        sesion.setFechaApertura(fechaApertura != null ? fechaApertura : LocalDateTime.now());
        sesion.setMontoInicial(montoInicial);
        sesion.setMontoEsperado(montoInicial);
        sesion.setEstado("ABIERTA");
        return sesion;
    }

    public SesionCajaDomain(Long id, CajaDomain caja, UsuarioDomain usuario, LocalDateTime fechaApertura,
                            BigDecimal montoInicial, LocalDateTime fechaCierre, BigDecimal montoEsperado,
                            BigDecimal montoReal, BigDecimal diferencia, String estado) {
        this.id = id;
        this.caja = caja;
        this.usuario = usuario;
        this.fechaApertura = fechaApertura;
        this.montoInicial = montoInicial != null ? montoInicial : BigDecimal.ZERO;
        this.fechaCierre = fechaCierre;
        this.montoEsperado = montoEsperado != null ? montoEsperado : this.montoInicial;
        this.montoReal = montoReal;
        this.diferencia = diferencia;
        this.estado = (estado != null) ? estado.toUpperCase() : "ABIERTA";
    }

    public void registrarIngreso(BigDecimal monto) {
        validarSesionAbierta();
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a ingresar debe ser mayor a cero.");
        }
        BigDecimal actual = (this.montoEsperado != null) ? this.montoEsperado : this.montoInicial;
        this.montoEsperado = actual.add(monto);
    }

    public void registrarRetiro(BigDecimal monto) {
        validarSesionAbierta();
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor a cero.");
        }
        BigDecimal actual = (this.montoEsperado != null) ? this.montoEsperado : this.montoInicial;
        if (actual.compareTo(monto) < 0) {
            throw new SaldoInsuficienteCajaException(actual, monto);
        }
        this.montoEsperado = actual.subtract(monto);
    }

    public void cerrar(BigDecimal montoRealContado, LocalDateTime fechaCierre) {
        validarSesionAbierta();
        if (montoRealContado == null || montoRealContado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto real contado no puede ser negativo.");
        }

        BigDecimal esperado = (this.montoEsperado != null) ? this.montoEsperado : this.montoInicial;
        this.montoReal = montoRealContado;
        this.diferencia = montoRealContado.subtract(esperado);
        this.fechaCierre = (fechaCierre != null) ? fechaCierre : LocalDateTime.now();
        this.estado = "CERRADA";

        if (this.caja != null) {
            this.caja.cerrar();
        }
    }

    private void validarSesionAbierta() {
        if (!"ABIERTA".equalsIgnoreCase(this.estado)) {
            throw new IllegalStateException("La sesión de caja no se encuentra ABIERTA.");
        }
    }

    public boolean isAbierta() {
        return "ABIERTA".equalsIgnoreCase(this.estado);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CajaDomain getCaja() {
        return caja;
    }

    public void setCaja(CajaDomain caja) {
        this.caja = caja;
    }

    public UsuarioDomain getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDomain usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(BigDecimal montoInicial) {
        this.montoInicial = montoInicial;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getMontoEsperado() {
        return montoEsperado;
    }

    public void setMontoEsperado(BigDecimal montoEsperado) {
        this.montoEsperado = montoEsperado;
    }

    public BigDecimal getMontoReal() {
        return montoReal;
    }

    public void setMontoReal(BigDecimal montoReal) {
        this.montoReal = montoReal;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SesionCajaDomain that = (SesionCajaDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
