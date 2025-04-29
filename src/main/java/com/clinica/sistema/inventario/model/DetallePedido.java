package com.clinica.sistema.inventario.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "detallePedidos")
public class DetallePedido {

    @Id
    @Column(name = "idDetallePedido")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetallePedido;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio")
    private double precio;

    @Column(name = "total")
    private double total;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

}
