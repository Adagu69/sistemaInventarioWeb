package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Area;
import com.clinica.sistema.inventario.model.Estado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IAreaServicio {

    List<Area> listarAreas();

    List<Area> listarAreasActivas();

    Page<Area> findAll(Pageable pageable);

    Area obtenerAreaPorId(Long idArea);

    Area findOne(Long idArea);

    Optional<Area> findById(Long idArea);

    void save(Area area);

    void delete(Long idArea);
}
