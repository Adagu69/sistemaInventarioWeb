package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Categoria;
import com.clinica.sistema.inventario.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductoServicio {

    public List<Producto> listarProductos();

    public Page<Producto> findAll(Pageable pageable);

    public List<Producto> findAll();

    public Producto findOne(Long idProducto);

    public void save(Producto producto);

    public void delete(Long idProducto);

    Optional<Producto> findById(Long idProducto);

}
