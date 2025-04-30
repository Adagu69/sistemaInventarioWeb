package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.controlador.dto.MovimientoDTO;
import com.clinica.sistema.inventario.model.Inventario;
import com.clinica.sistema.inventario.model.Movimiento;
import com.clinica.sistema.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovimientoServicio {

    List<Movimiento> findAll();

    void save(Movimiento movimiento);

    Movimiento registrarMovimiento(Movimiento movimiento, String emailUsuario, MovimientoDTO movimientoDTO);

    Movimiento actualizarMovimiento(Movimiento movimiento);

    void eliminarMovimiento(Long idMovimiento);

    Movimiento buscarMovimiento(Long idMovimiento);

    List<Movimiento> buscarMovimientosPorProducto(Long idProducto);

    List<Movimiento> buscarMovimientosPorTipo(String tipo);

    List<Movimiento> buscarMovimientosPorEstado(String estado);

    List<Movimiento> buscarMovimientosPorMotivo(String motivo);

    Page<Movimiento> findAll(Pageable pageable);

    //registrarMovimientoCompleto




}
