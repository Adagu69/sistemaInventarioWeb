package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {

}
