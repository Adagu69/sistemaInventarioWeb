package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Area;
import com.clinica.sistema.inventario.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepositorio extends JpaRepository<Area, Long> {

    List<Area> findByEstado(Estado estado);
}
