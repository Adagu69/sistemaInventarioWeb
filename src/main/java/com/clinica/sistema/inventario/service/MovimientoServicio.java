package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Inventario;
import com.clinica.sistema.inventario.model.Movimiento;
import com.clinica.sistema.inventario.model.Producto;
import com.clinica.sistema.inventario.repository.InventarioRepositorio;
import com.clinica.sistema.inventario.repository.MovimientoRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoServicio implements IMovimientoServicio {

    @Autowired
    private MovimientoRepositorio movimientoRepository;

    @Autowired
    private InventarioServicio inventarioServicio;

    @Autowired
    private ProductoServicio productoServicio;

    private static final Logger logger = LoggerFactory.getLogger(MovimientoServicio.class);

    @Override
    public List<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }

    @Override
    public void save(Movimiento movimiento) {
        logger.info("Saving movimiento: {}", movimiento);
        movimientoRepository.save(movimiento);
    }


    @Transactional
    @Override
    public Movimiento registrarMovimiento(Movimiento movimiento, Inventario inventario) {
        if (movimiento == null || movimiento.getProducto() == null) {
            throw new IllegalArgumentException("Movimiento or Producto cannot be null");
        }

        // Ensure the product exists
        Producto producto = productoServicio.findById(movimiento.getProducto().getIdProducto())
                .orElseThrow(() -> new EntityNotFoundException("Producto not found"));

        // Save the movement
        Movimiento nuevoMovimiento = movimientoRepository.save(movimiento);

        // Update inventory
        if (inventario == null) {
            inventario = new Inventario();
            inventario.setProducto(producto);
            inventario.setCantidad(0);
            inventario.setEstado("ACTIVO");
        }

        // Adjust inventory based on movement type
        if ("ENTRADA".equals(movimiento.getTipo())) {
            inventario.setCantidad(inventario.getCantidad() + movimiento.getCantidad());
        } else if ("SALIDA".equals(movimiento.getTipo())) {
            if (inventario.getCantidad() < movimiento.getCantidad()) {
                throw new IllegalArgumentException("Cantidad insuficiente en inventario");
            }
            inventario.setCantidad(inventario.getCantidad() - movimiento.getCantidad());
        }

        // Save updated inventory
        inventarioServicio.save(inventario);

        return nuevoMovimiento;
    }

    @Override
    public Movimiento actualizarMovimiento(Movimiento movimiento) {
        return null;
    }

    @Override
    public void eliminarMovimiento(Long idMovimiento) {

    }

    @Override
    public Movimiento buscarMovimiento(Long idMovimiento) {
        return null;
    }

    @Override
    public List<Movimiento> buscarMovimientosPorProducto(Long idProducto) {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarMovimientosPorTipo(String tipo) {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarMovimientosPorEstado(String estado) {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarMovimientosPorMotivo(String motivo) {
        return List.of();
    }

    @Autowired
    private MovimientoRepositorio movimientoRepositorio;

    public List<Movimiento> findByTipo(String tipo) {
        return movimientoRepositorio.findByTipo(tipo);
    }

}

