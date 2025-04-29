package com.clinica.sistema.inventario.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data // anotacion que genera los metodos getter, setter, equals, hashcode y toString
@NoArgsConstructor // anotacion que genera un constructor sin argumentos
@AllArgsConstructor // anotacion que genera un constructor con todos los argumentos
@ToString
@EqualsAndHashCode
@Entity // anotacion que indica que la clase es una entidad
@Table(name = "inventarios")
public class Inventario {

    @Id
    @Column(name = "idInventario")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // anotacion que indica que el valor es autoincrementable
    private Long idInventario;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio")
    private double precio;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "estado")
    private String estado;

    @ManyToOne // anotacion que indica que la relacion es de muchos a uno
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
