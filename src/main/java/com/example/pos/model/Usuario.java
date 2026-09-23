package com.example.pos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(name = "contraseña", nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 30)
    private String rol = "VENDEDOR";

    @Column(nullable = false)
    private Boolean activo = true;
}
