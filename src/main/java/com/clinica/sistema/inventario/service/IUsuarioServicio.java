package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.controlador.dto.UsuarioRegistroDTO;
import com.clinica.sistema.inventario.model.Area;
import com.clinica.sistema.inventario.model.Usuario;
import org.bouncycastle.pqc.crypto.newhope.NHOtherInfoGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUsuarioServicio {

    public Usuario guardar(UsuarioRegistroDTO registroDTO,Long idArea);


    public List<Usuario> listarUsuarios();

    public Page<Usuario> findAll(Pageable pageable);

    public Usuario findOne(Long id);

    Optional<Usuario> findById(Long id);

    public void save(Usuario usuario);

    public void delete(Long id);

    Usuario findByEmail(String emailUsuario);

//    public Usuario findByUsername(String username);
}
