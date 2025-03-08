package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Solicitud;
import com.clinica.sistema.inventario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface SolicitudRepositorio extends JpaRepository<Solicitud, Long> {

    @EntityGraph(attributePaths = {"usuario", "producto"})
    List<Solicitud> findAllByOrderByFechaDesc();
    List<Solicitud> findByUsuario_IdUsuario(Long usuarioId);
}
