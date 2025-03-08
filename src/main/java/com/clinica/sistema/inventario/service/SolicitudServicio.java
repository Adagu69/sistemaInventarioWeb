package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Solicitud;
import com.clinica.sistema.inventario.repository.SolicitudRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SolicitudServicio implements ISolicitudServicio {

    @Autowired
    private SolicitudRepositorio solicitudRepositorio;

    @Override
    public List<Solicitud> findByUsuario(Long idUsuario) {

        return solicitudRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Solicitud> findAll() {
        return solicitudRepositorio.findAllByOrderByFechaDesc();
    }

    @Override
    public Solicitud findOne(Long idSolicitud) {
        return solicitudRepositorio.findById(idSolicitud).orElse(null);
    }

    @Override
    public void save(Solicitud solicitud) {
        solicitudRepositorio.save(solicitud);
    }

    @Override
    public void delete(Long idSolicitud) {
        solicitudRepositorio.deleteById(idSolicitud);
    }

    @Override
    public Optional<Solicitud> findById(Long idSolicitud) {
        return solicitudRepositorio.findById(idSolicitud);
    }
}
