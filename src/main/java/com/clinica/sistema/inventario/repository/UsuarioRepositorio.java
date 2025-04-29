package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Estado;
import com.clinica.sistema.inventario.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    // Método para buscar por nombre, apellido, correo electrónico o estado
    Page<Usuario> findByNombreContainingOrApellidoContainingOrEmailContainingOrEstadoIgnoreCase(
            String nombre, String apellido, String email, Estado estado, Pageable pageable);

    // Otro método de búsqueda por email (ya existe en tu código)
    Usuario findByEmail(String email);

    // Método para buscar por nombre
    Page<Usuario> findByNombreContaining(String nombre, Pageable pageable);

    boolean existsByEmail(String email);

}
