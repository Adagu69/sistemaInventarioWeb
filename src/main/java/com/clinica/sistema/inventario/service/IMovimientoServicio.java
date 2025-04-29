package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.controlador.dto.MovimientoDTO;
import com.clinica.sistema.inventario.model.Inventario;
import com.clinica.sistema.inventario.model.Movimiento;
import com.clinica.sistema.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovimientoServicio {

    public List<Movimiento> findAll();

    public void save(Movimiento movimiento);

    public Movimiento registrarMovimiento(Movimiento movimiento, String emailUsuario, MovimientoDTO movimientoDTO);

    public Movimiento actualizarMovimiento(Movimiento movimiento);

    public void eliminarMovimiento(Long idMovimiento);

    public Movimiento buscarMovimiento(Long idMovimiento);

    public List<Movimiento> buscarMovimientosPorProducto(Long idProducto);

    public List<Movimiento> buscarMovimientosPorTipo(String tipo);

    public List<Movimiento> buscarMovimientosPorEstado(String estado);

    public List<Movimiento> buscarMovimientosPorMotivo(String motivo);

    //registrarMovimientoCompleto




}
