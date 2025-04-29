package com.clinica.sistema.inventario.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Data
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @Column(name = "idRol")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;


    @Column(name = "nombre")
    private String nombre;

    public Rol(Long idRol, String nombre) {
        super();
        this.idRol = idRol;
        this.nombre = nombre;
    }

    public Rol() {

    }
    public Rol(String nombre) {
        super();
        this.nombre = nombre;
    }
}
