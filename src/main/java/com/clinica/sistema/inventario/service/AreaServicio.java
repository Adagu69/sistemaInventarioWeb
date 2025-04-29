package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Area;
import com.clinica.sistema.inventario.model.Estado;
import com.clinica.sistema.inventario.repository.AreaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AreaServicio implements IAreaServicio {

    @Autowired
    private AreaRepositorio areaRepositorio;


    @Override
    public List<Area> listarAreas() {
        return areaRepositorio.findAll();
    }

    @Override
    public List<Area> listarAreasActivas() {
        return areaRepositorio.findByEstado(Estado.ACTIVO);
    }

    @Override
    public Page<Area> findAll(Pageable pageable) {
        return areaRepositorio.findAll(pageable);
    }

    @Override
    public Area obtenerAreaPorId(Long id) {
        return areaRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Area no encontrada"));
    }

    @Override
    public Area findOne(Long idArea) {
        return areaRepositorio.findById(idArea).orElse(null);
    }

    @Override
    public Optional<Area> findById(Long id) {
        return areaRepositorio.findById(id);
    }

    @Override
    public void save(Area area) {
        areaRepositorio.save(area);
    }

    @Override
    public void delete(Long id) {
        areaRepositorio.deleteById(id);
    }
}
