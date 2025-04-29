package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepositorio extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String roleUser);
}
