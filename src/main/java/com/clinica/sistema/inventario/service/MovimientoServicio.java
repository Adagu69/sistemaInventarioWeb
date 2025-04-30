package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.controlador.dto.MovimientoDTO;
import com.clinica.sistema.inventario.model.*;
import com.clinica.sistema.inventario.repository.InventarioRepositorio;
import com.clinica.sistema.inventario.repository.MovimientoRepositorio;
import com.clinica.sistema.inventario.repository.UsuarioRepositorio;
import com.clinica.sistema.inventario.util.RedondeoUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoServicio implements IMovimientoServicio {

    @Autowired
    private MovimientoRepositorio movimientoRepository;

    @Autowired
    private InventarioServicio inventarioServicio;

    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private AreaServicio areaServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    private static final Logger logger = LoggerFactory.getLogger(MovimientoServicio.class);

    private Usuario obtenerUsuarioValidado(String email) {
        Usuario usuario = usuarioRepositorio.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con email: " + email);
        }
        return usuario;
    }

    private Area determinarAreaMovimiento(Usuario usuario, Long areaId, boolean isAdmin) {
        if (isAdmin) {
            return areaServicio.obtenerAreaPorId(areaId);
        }

        Area areaUsuario = usuario.getArea();
        if (areaUsuario == null) {
            throw new IllegalStateException("El usuario no tiene un área asignada");
        }
        return areaUsuario;
    }

    private Producto obtenerProductoValidado(Long productoId) {
        return productoServicio.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));
    }

    private void procesarMovimientoEInventario(Movimiento movimiento) {
        Inventario inventario = inventarioServicio.findByProducto(movimiento.getProducto())
                .orElseGet(() -> crearNuevoInventario(movimiento.getProducto()));

        actualizarInventarioSegunMovimiento(inventario, movimiento);

        // Configurar movimiento
        //movimiento.setFecha(LocalDateTime.now());
        movimiento.setTotal(movimiento.getCantidad() * movimiento.getPrecio());
        movimiento.setEstado("ACTIVO");

        // Guardar cambios en inventario
        inventarioServicio.save(inventario);
    }

    private Inventario crearNuevoInventario(Producto producto) {
        Inventario nuevo = new Inventario();
        nuevo.setProducto(producto);
        nuevo.setCantidad(0);
        nuevo.setPrecio(0.0);
        nuevo.setEstado("ACTIVO");
        nuevo.setFecha(LocalDate.now());
        return nuevo;
    }

    private void actualizarInventarioSegunMovimiento(Inventario inventario, Movimiento movimiento) {
        if ("ENTRADA".equals(movimiento.getTipo())) {
            inventario.setCantidad(inventario.getCantidad() + movimiento.getCantidad());
            inventario.setPrecio(RedondeoUtil.redondear(movimiento.getPrecio()));
        } else if ("SALIDA".equals(movimiento.getTipo())) {
            validarStockSuficiente(inventario, movimiento.getCantidad());
            inventario.setCantidad(inventario.getCantidad() - movimiento.getCantidad());
        }
        inventario.setFecha(LocalDate.now());
    }

    private void validarStockSuficiente(Inventario inventario, int cantidadSalida) {
        if (inventario.getCantidad() < cantidadSalida) {
            throw new IllegalArgumentException(
                    String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                            inventario.getCantidad(), cantidadSalida));
        }
    }


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
    public Movimiento registrarMovimiento(Movimiento movimiento,
                                          String emailUsuario,
                                          MovimientoDTO movimientoDTO) {

        // Validar y configurar movimiento
        Usuario usuario = obtenerUsuarioValidado(emailUsuario);
        boolean isAdmin = usuario.isAdmin();
        // Validar el DTO según el tipo de usuario
        movimientoDTO.validarArea(isAdmin);

        Area area = determinarAreaMovimiento(usuario, movimientoDTO.getAreaId(), isAdmin);
        Producto producto = obtenerProductoValidado(movimientoDTO.getProductoId());

        movimiento.setArea(area);
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);

        // Procesar movimiento e inventario
        procesarMovimientoEInventario(movimiento);

        return movimientoRepository.save(movimiento);
    }



    @Override
    public Movimiento actualizarMovimiento(Movimiento movimiento) {
        if (!movimientoRepository.existsById(movimiento.getIdMovimiento())) {
            throw new IllegalArgumentException("Movimiento no encontrado con ID: " + movimiento.getIdMovimiento());
        }
        return movimientoRepository.save(movimiento);
    }

    @Override
    public void eliminarMovimiento(Long idMovimiento) {
        if (!movimientoRepository.existsById(idMovimiento)) {
            throw new IllegalArgumentException("Movimiento no encontrado con ID: " + idMovimiento);
        }
        movimientoRepository.deleteById(idMovimiento);
    }

    @Override
    public Movimiento buscarMovimiento(Long idMovimiento) {
        return movimientoRepository.findById(idMovimiento)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado con ID: " + idMovimiento));
    }

    @Override
    public List<Movimiento> buscarMovimientosPorProducto(Long idProducto) {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarMovimientosPorTipo(String tipo) {
        return movimientoRepository.findByTipo(tipo);
    }

    @Override
    public List<Movimiento> buscarMovimientosPorEstado(String estado) {
        return List.of();
    }

    @Override
    public List<Movimiento> buscarMovimientosPorMotivo(String motivo) {
        return List.of();
    }

    @Override
    public Page<Movimiento> findAll(Pageable pageable) {
        return movimientoRepository.findAll(pageable);
    }

    @Autowired
    private MovimientoRepositorio movimientoRepositorio;

    public List<Movimiento> findByTipo(String tipo) {
        return movimientoRepositorio.findByTipo(tipo);
    }

}

