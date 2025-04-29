package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface InventarioRepositorio extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoIdProducto(Long idProducto);

    List<Inventario> findByProductoNombreContainingIgnoreCase(String nombreProducto);

    Inventario findByIdInventario(Long idInventario);
}