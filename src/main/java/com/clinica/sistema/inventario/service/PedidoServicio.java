package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.model.Pedido;
import com.clinica.sistema.inventario.repository.PedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoServicio implements IPedidoServicio {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Override
    public List<Pedido> findByUsuario(Long idUsuario) {
        return pedidoRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoRepositorio.findAllByOrderByFechaDesc();
    }

    @Override
    public Pedido findOne(Long idPedido) {
        return pedidoRepositorio.findById(idPedido).orElse(null);
    }

    @Override
    public void save(Pedido pedido) {
        pedidoRepositorio.save(pedido);
    }

    @Override
    public void delete(Long idPedido) {
        pedidoRepositorio.deleteById(idPedido);
    }

    @Override
    public Optional<Pedido> findById(Long idPedido) {
        return pedidoRepositorio.findById(idPedido);
    }
}
