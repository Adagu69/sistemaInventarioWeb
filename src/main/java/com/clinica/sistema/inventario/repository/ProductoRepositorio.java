package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
    // Método para buscar productos por nombre
    Page<Producto> findByNombreContainingIgnoreCase(String nombreProducto, Pageable pageable);

    boolean existsByNombreAndProveedorIdProveedor(String nombre, Long idProveedor);



}
