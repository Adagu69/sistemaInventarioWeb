package com.clinica.sistema.inventario.model;



import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Long idUsuario;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotNull
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @NotNull
    @Email(message = "El correo no es válido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "idUsuario"),
            inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "idRol")
    )
    private Collection<Rol> roles = new ArrayList<>();

    @OneToMany(mappedBy = "usuario") // Relación inversa con Movimiento
    private List<Movimiento> movimientos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.ACTIVO; // Estado predeterminado a "ACTIVO"

    @Embedded
    private UsuarioFecha usuarioFecha;  // Campo embebido de tipo UsuarioFecha

    // Constructor sin argumentos (publico)
    public Usuario() {
        this.usuarioFecha = new UsuarioFecha();  // Inicializa con la fecha actual
    }

    // Constructor con todos los campos
    public Usuario(Long idUsuario, String nombre, String apellido, String email, String password, Estado estado, UsuarioFecha usuarioFecha) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.estado = estado;
        this.usuarioFecha = usuarioFecha;
    }

    // Constructor con todos los campos, incluyendo UsuarioFecha
    public Usuario(String nombre, String apellido, String email, String password,Collection<Rol> roles, Estado estado, UsuarioFecha usuarioFecha) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.estado = estado != null ? estado : Estado.ACTIVO; // Si no se pasa un estado, se asigna "ACTIVO"
        this.usuarioFecha = usuarioFecha != null ? usuarioFecha : new UsuarioFecha(); // Asegura un valor por defecto
    }

    // Métodos getter y setter para fecha (usuarioFecha)
    public UsuarioFecha getUsuarioFecha() {
        return usuarioFecha;
    }

    public void setUsuarioFecha(UsuarioFecha usuarioFecha) {
        this.usuarioFecha = usuarioFecha;
    }

    public void setFecha(LocalDate fecha) {
        if (this.usuarioFecha == null) {
            this.usuarioFecha = new UsuarioFecha();
        }
        this.usuarioFecha.setFecha(fecha);
    }

    public boolean hasRole(String role) {
        return roles != null && roles.stream()
                .anyMatch(r -> r.getNombre().equals(role));
    }


    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }
}
