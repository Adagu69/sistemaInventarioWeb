package com.clinica.sistema.inventario.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="areas")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_area")
    private Long idArea;

    @Column(name="nombre")
    private String nombre;

    @Column(name="descripciones")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVO;

    @OneToMany(mappedBy = "area")
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "area")
    private List<Movimiento> movimientos;
}
