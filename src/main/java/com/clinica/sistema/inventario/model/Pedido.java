package com.clinica.sistema.inventario.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @Column(name = "idPedido")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Column(name = "fecha")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fecha;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "motivo")
    private String motivo;

    @OneToMany(mappedBy = "pedido")
    private Set<DetallePedido> detalles;

}
