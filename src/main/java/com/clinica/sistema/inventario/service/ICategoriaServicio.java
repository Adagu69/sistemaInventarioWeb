package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ICategoriaServicio {

    public List<Categoria> listarCategorias();

    public Page<Categoria> findAll(Pageable pageable);

    public Categoria findOne(Long id);

    Optional<Categoria> findById(Long id);

    public void save(Categoria categoria);

    public void delete(Long id);
}
