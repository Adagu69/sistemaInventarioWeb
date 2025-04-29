package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Categoria;
import com.clinica.sistema.inventario.repository.CategoriaRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServicio implements ICategoriaServicio {

    @Autowired
    private CategoriaRepositorio CategoriaRepositorio;


    @Override
    public List<Categoria> listarCategorias() {
        return CategoriaRepositorio.findAll();
    }

    @Override
    public Page<Categoria> findAll(Pageable pageable) {
        return CategoriaRepositorio.findAll(pageable);
    }

    @Override
    public Categoria findOne(Long id) {

        return CategoriaRepositorio.findById(id).orElse(null);
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return CategoriaRepositorio.findById(id);
    }

    @Override
    public void save(Categoria categoria) {

        CategoriaRepositorio.save(categoria);
    }

    @Override
    public void delete(Long id) {
        CategoriaRepositorio.deleteById(id);
    }
}
