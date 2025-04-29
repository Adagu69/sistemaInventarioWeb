package com.clinica.sistema.inventario.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @Column(name = "idProveedor")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProveedor;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "correo")
    private String correo;

    @OneToMany(mappedBy = "proveedor")
    private Set<Producto> productos;
}
