package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Pedido;

import java.util.List;
import java.util.Optional;

public interface IPedidoServicio {

    List<Pedido> findByUsuario(Long idUsuario);

    List<Pedido> findAll();

    Pedido findOne(Long idPedido);

    void save(Pedido pedido);

    void delete(Long idPedido);

    Optional<Pedido> findById(Long idPedido);


}
