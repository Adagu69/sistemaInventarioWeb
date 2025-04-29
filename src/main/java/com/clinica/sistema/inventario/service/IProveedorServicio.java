package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProveedorServicio {

    public List<Proveedor> listarProveedores();

    public Page<Proveedor> findAll(Pageable pageable);

    List<Proveedor> findAll();

    public Proveedor findOne(Long idProveedor);

    Optional<Proveedor> findById(Long idProveedor);

    public void save(Proveedor proveedor);

    public void delete(Long idProveedor);

}
