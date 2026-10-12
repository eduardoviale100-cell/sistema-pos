package com.example.pos.infrastructure.output.persistence.caja;

import com.example.pos.domain.model.caja.*;
import com.example.pos.domain.model.caja.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA para la tabla 'cajas' en la capa de persistencia.
 */
@Entity
@Table(name = "cajas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CajaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String estado;

    @PrePersist
    protected void onCreate() {
        if (estado == null || estado.isBlank()) {
            estado = "CERRADA";
        }
    }
}
