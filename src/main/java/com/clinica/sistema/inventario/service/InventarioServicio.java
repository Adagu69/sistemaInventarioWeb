package com.clinica.sistema.inventario.service;


import com.clinica.sistema.inventario.model.Inventario;
import com.clinica.sistema.inventario.model.Producto;
import com.clinica.sistema.inventario.repository.InventarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioServicio implements IInventarioServicio {

    @Autowired
    private InventarioRepositorio inventarioRepositorio;

    @Override
    public List<Inventario> findByProductoNombreContainingIgnoreCase(String nombreProducto) {
        return inventarioRepositorio.findByProductoNombreContainingIgnoreCase(nombreProducto);
    }

    @Override
    public Inventario findByProductoIdProducto(Long idProducto) {
        return inventarioRepositorio.findByProductoIdProducto(idProducto)
                .orElse(null);
    }

    @Override
    public List<Inventario> findAll() {
        return inventarioRepositorio.findAll();
    }

    @Override
    public Inventario findByIdInventario(Long idInventario) {
        return inventarioRepositorio.findByIdInventario(idInventario);
    }

    @Override
    public Inventario save(Inventario inventario) {
        return inventarioRepositorio.save(inventario);
    }

    @Override
    public Optional<Inventario> findByProducto(Producto producto) {
        return inventarioRepositorio.findByProductoIdProducto(producto.getIdProducto());
    }
}
