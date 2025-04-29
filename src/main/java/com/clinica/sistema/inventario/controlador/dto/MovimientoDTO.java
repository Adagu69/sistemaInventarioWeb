package com.clinica.sistema.inventario.controlador.dto;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class MovimientoDTO {
    @NotBlank private String tipo; // ENTRADA o SALIDA
    @Min(1) private int cantidad;
    @DecimalMin("0.01") private double precio;
    @NotBlank private String motivo;
    @NotNull private Long productoId;
    private Long areaId; // No requerido para usuarios normales

    public MovimientoDTO() {
    }

    public MovimientoDTO(String tipo, int cantidad, double precio, String motivo, Long productoId, Long areaId) {
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.motivo = motivo;
        this.productoId = productoId;
        this.areaId = areaId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public void validarArea(boolean isAdmin) {
        if (isAdmin && areaId == null) {
            throw new IllegalArgumentException("El administrador debe seleccionar un área");
        }
    }
}
