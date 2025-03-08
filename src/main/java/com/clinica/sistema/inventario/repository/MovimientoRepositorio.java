package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepositorio extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByTipo(String tipo);

}
