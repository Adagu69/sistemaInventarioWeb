package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Categoria;
import com.clinica.sistema.inventario.model.DetalleProducto;
import com.clinica.sistema.inventario.model.Producto;
import com.clinica.sistema.inventario.repository.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServicio implements IProductoServicio {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Override
    public List<Producto> listarProductos() {
        List<Producto> productos = productoRepositorio.findAll();
        return productos;
    }

    @Override
    public Page<Producto> findAll(Pageable pageable) {
        return productoRepositorio.findAll(pageable);
    }

    @Override
    public List<Producto> findAll() {
        return productoRepositorio.findAll();
    }

    @Override
    public Producto findOne(Long idProducto) {
        return productoRepositorio.findById(idProducto).orElse(null);
    }

    @Override
    public void save(Producto producto) {
        boolean duplicado = productoRepositorio.existsByNombreAndProveedorIdProveedor(
                producto.getNombre(),
                producto.getProveedor().getIdProveedor()
        );

        if (duplicado) {
            throw new RuntimeException("Ya existe un producto con ese nombre y proveedor. Por favor elija otro.");
        }

        productoRepositorio.save(producto);
    }

    @Override
    public void delete(Long idProducto) {
        productoRepositorio.deleteById(idProducto);
    }

    @Override
    public Optional<Producto> findById(Long idProducto) {
        return productoRepositorio.findById(idProducto);
    }

    public Page<Producto> buscarUsuariosPorNombre(String nombreProducto, Pageable pageable) {
        return productoRepositorio.findByNombreContainingIgnoreCase(nombreProducto, pageable);
    }

    public boolean existeProductoPorNombreYProveedor(String nombre, Long proveedorId) {
        return productoRepositorio.existsByNombreAndProveedorIdProveedor(nombre, proveedorId);
    }

    public boolean tieneDetalleProducto(Long idProducto) {
        return productoRepositorio.findById(idProducto)
                .map(p -> p.getDetalleProducto() != null)
                .orElse(false);
    }

    public void guardarDetalleProducto(Long idProducto, String especificaciones, String garantia) {
        Producto producto = productoRepositorio.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetalleProducto detalle = producto.getDetalleProducto();
        if (detalle == null) {
            detalle = new DetalleProducto();
            detalle.setProducto(producto);
        }

        detalle.setEspecificaciones(especificaciones);
        detalle.setGarantia(garantia);
        detalle.setCantidad(0);
        detalle.setPrecio(0.0);
        detalle.setTotal(0.0);

        producto.setDetalleProducto(detalle);
        productoRepositorio.save(producto); // Cascada se encarga de guardar también el detalle
    }

}
