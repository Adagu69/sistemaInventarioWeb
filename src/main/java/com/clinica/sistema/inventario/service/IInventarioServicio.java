package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Inventario;
import com.clinica.sistema.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface IInventarioServicio {

    List<Inventario> findByProductoNombreContainingIgnoreCase(String nombreProducto);
    Inventario findByProductoIdProducto(Long idProducto);
    List<Inventario> findAll(); // Added method
    Inventario findByIdInventario(Long idInventario); // Changed method name
    Inventario save(Inventario inventario);
    Page<Inventario> findAll(Pageable pageable);
    Optional<Inventario> findByProducto(Producto producto);



}
