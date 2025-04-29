package com.clinica.sistema.inventario.repository;

import com.clinica.sistema.inventario.model.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"usuario", "producto"})
    List<Pedido> findAllByOrderByFechaDesc();
    List<Pedido> findByUsuario_IdUsuario(Long usuarioId);
}
