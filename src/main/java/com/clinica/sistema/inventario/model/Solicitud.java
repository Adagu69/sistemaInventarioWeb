package com.clinica.sistema.inventario.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @Column(name = "idSolicitud")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;

    @Column(name = "fecha")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fecha;

    @Column(name = "cantidad")
    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;


}
