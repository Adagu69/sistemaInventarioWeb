package com.clinica.sistema.inventario.model;


import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @Column(name = "idMovimiento")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio")
    private double precio;

    @Column(name = "total")
    private double total;

    @Column(name = "motivo")
    private String motivo; // Corregí el nombre para que coincida con mayúsculas/minúsculas

    @Column(name = "estado")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id") // Nueva relación con Usuario
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    // Métodos adicionales si necesitas lógica específica
    public void calcularTotal() {
        this.total = this.cantidad * this.precio;
    }

    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.fecha.format(formatter);
    }

}
