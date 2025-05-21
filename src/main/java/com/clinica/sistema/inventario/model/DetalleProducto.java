package com.clinica.sistema.inventario.model;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "detalleProductos")
public class DetalleProducto {

    @Id
    @Column(name = "idDetalleProducto")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleProducto;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio")
    private double precio;

    @Column(name = "total")
    private double total;

    @OneToOne
    @JoinColumn(name = "producto_id", nullable = false, unique = true)
    private Producto producto;

    @Column(name = "especificaciones")
    private String especificaciones;

    @Column(name = "garantia")
    private String garantia;
}
