package com.clinica.sistema.inventario.service;

import com.clinica.sistema.inventario.controlador.UsuarioControlador;
import com.clinica.sistema.inventario.controlador.dto.UsuarioRegistroDTO;
import com.clinica.sistema.inventario.model.*;
import com.clinica.sistema.inventario.repository.AreaRepositorio;
import com.clinica.sistema.inventario.repository.RolRepositorio;
import com.clinica.sistema.inventario.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio implements IUsuarioServicio, UserDetailsService {


    private final  UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AreaRepositorio areaRepositorio;

    @Autowired
    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio,RolRepositorio rolRepositorio,
                           BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.rolRepositorio = rolRepositorio;
    }

    @Override
    public Usuario guardar(UsuarioRegistroDTO registroDTO,Long idArea) {


        Rol rolUser = rolRepositorio.findByNombre("ROLE_USER")
                .orElseThrow(()-> new RuntimeException("Rol USER no encontrado en la Base de Datos"));

        // Verificar si el email ya existe
        if (usuarioRepositorio.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        // Obtener el área seleccionada
        Area area = areaRepositorio.findById(idArea).orElseThrow(() -> new RuntimeException("Área no encontrada en la Base de Datos"));

        // Obtener la fecha actual si no se pasa una en el DTO
        LocalDate fecha = registroDTO.getFecha() != null ? registroDTO.getFecha() : LocalDate.now();

        // Crear un objeto UsuarioFecha con la fecha
        UsuarioFecha usuarioFecha = new UsuarioFecha(fecha);

        // Crear el usuario con estado ACTIVO y la fecha embebida
        Usuario usuario = new Usuario(
                registroDTO.getNombre(),
                registroDTO.getApellido(),
                registroDTO.getEmail(),
                passwordEncoder.encode(registroDTO.getPassword()),
                Collections.singletonList(rolUser),
                Estado.ACTIVO,
                new UsuarioFecha(fecha));

        // Asignar el área al usuario
        usuario.setArea(area);

        return usuarioRepositorio.save(usuario);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByEmail(username);
        if (usuario == null || usuario.getEstado() != Estado.ACTIVO) {
            throw new UsernameNotFoundException("Usuario inactivo o no encontrado");
        }
        return new User(usuario.getEmail(),
                usuario.getPassword(),
                mapearAutoridadesRoles(usuario.getRoles()));
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepositorio.findAll(pageable);
    }

    @Override
    public Usuario findOne(Long id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepositorio.findById(id);
    }

    @Override
    public void save(Usuario usuario) {
        usuarioRepositorio.save(usuario);
    }

    @Override
    public void delete(Long id) {
        usuarioRepositorio.deleteById(id);
    }

    @Override
    public Usuario findByEmail(String emailUsuario) {
        return usuarioRepositorio.findByEmail(emailUsuario);
    }


    private Collection<? extends GrantedAuthority> mapearAutoridadesRoles(Collection<Rol> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                .collect(Collectors.toList());
    }

    public void eliminarUsuarioLogicamente(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            // Cambiar el estado del usuario a INACTIVO
            usuario.setEstado(Estado.INACTIVO);  // Esto es clave: Cambiar el estado
            usuarioRepositorio.save(usuario);    // Guardar los cambios
        }
    }

    // Archivo: UsuarioServicio.java

    public Page<Usuario> buscarUsuarios(String buscar, Pageable pageable) {
        // Usamos un query personalizado para buscar por nombre, apellido, email o estado
        return usuarioRepositorio.findByNombreContainingOrApellidoContainingOrEmailContainingOrEstadoIgnoreCase(
                buscar, buscar, buscar, Estado.valueOf(buscar.toUpperCase()), pageable);
    }

    // Método para buscar por nombre
    public Page<Usuario> buscarUsuariosPorNombre(String nombre, Pageable pageable) {
        return usuarioRepositorio.findByNombreContaining(nombre, pageable); // Buscar por nombre
    }

}

