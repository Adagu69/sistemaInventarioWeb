package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Proveedor;
import com.clinica.sistema.inventario.repository.ProveedorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServicio implements IProveedorServicio {

    @Autowired
    private ProveedorRepositorio proveedorRepositorio;

    @Override
    public List<Proveedor> listarProveedores() {
        return proveedorRepositorio.findAll();
    }

    @Override
    public Page<Proveedor> findAll(Pageable pageable) {
        return proveedorRepositorio.findAll(pageable);
    }

    @Override
    public List<Proveedor> findAll() {
        return proveedorRepositorio.findAll();
    }

    @Override
    public Proveedor findOne(Long idProveedor) {
        return proveedorRepositorio.findById(idProveedor).orElse(null);
    }

    @Override
    public Optional<Proveedor> findById(Long idProveedor) {
        return proveedorRepositorio.findById(idProveedor);
    }

    @Override
    public void save(Proveedor proveedor) {
        proveedorRepositorio.save(proveedor);
    }

    @Override
    public void delete(Long idProveedor) {
        proveedorRepositorio.deleteById(idProveedor);
    }

}
