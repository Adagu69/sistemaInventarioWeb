package com.clinica.sistema.inventario.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class UsuarioFecha {

    private LocalDate fecha;  // Una sola fecha de registro

    // Constructor por defecto
    public UsuarioFecha() {
        this.fecha = LocalDate.now();  // Valor por defecto, la fecha actual
    }

    // Constructor con parámetro
    public UsuarioFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
