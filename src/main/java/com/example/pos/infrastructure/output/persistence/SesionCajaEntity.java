package com.example.pos.infrastructure.output.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla 'sesiones_caja'.
 */
@Entity
@Table(name = "sesiones_caja")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionCajaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id", nullable = false)
    private CajaEntity caja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "monto_inicial", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoInicial;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto_esperado", precision = 10, scale = 2)
    private BigDecimal montoEsperado;

    @Column(name = "monto_real", precision = 10, scale = 2)
    private BigDecimal montoReal;

    @Column(name = "diferencia", precision = 10, scale = 2)
    private BigDecimal diferencia;

    @Column(nullable = false, length = 20)
    private String estado;

    @PrePersist
    protected void onCreate() {
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "ABIERTA";
        }
        if (montoInicial == null) {
            montoInicial = BigDecimal.ZERO;
        }
        if (montoEsperado == null) {
            montoEsperado = montoInicial;
        }
    }
}
