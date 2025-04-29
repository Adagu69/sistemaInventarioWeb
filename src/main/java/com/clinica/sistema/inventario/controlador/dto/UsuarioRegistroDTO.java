package com.clinica.sistema.inventario.controlador.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

public class UsuarioRegistroDTO {

    private Long idUsuario;
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;
    @NotEmpty(message = "El apellido no puede estar vacío")
    private String apellido;
    @NotEmpty(message = "El email no puede estar vacío")
    @Email(message = "Ingrese un email válido")
    private String email;
    @NotEmpty(message = "La contraseña no puede estar vacía")
    private String password;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    private Long areaId;// Nuevo campo para la fecha

    public UsuarioRegistroDTO(Long idUsuario, String nombre, String apellido, String email, String password, LocalDate fecha, Long areaId) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.fecha = fecha;
        this.areaId = areaId;
    }

    public UsuarioRegistroDTO() {
    }

    //getters and setters

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    // Getters y setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


}
