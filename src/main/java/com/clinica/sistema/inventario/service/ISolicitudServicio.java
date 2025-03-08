package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ISolicitudServicio {

    //find by usuario
    List<Solicitud> findByUsuario(Long idUsuario);

    List<Solicitud> findAll();

    // Buscar una solicitud por ID
    Solicitud findOne(Long idSolicitud);

    // Guardar una nueva solicitud
    void save(Solicitud solicitud);

    // Eliminar una solicitud por ID
    void delete(Long idSolicitud);

    // Buscar una solicitud por ID usando Optional
   Optional<Solicitud> findById(Long idSolicitud);

}
