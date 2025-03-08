// Archivo: UsuarioControlador.java
package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.controlador.dto.UsuarioRegistroDTO;
import com.clinica.sistema.inventario.model.Categoria;
import com.clinica.sistema.inventario.model.Estado;
import com.clinica.sistema.inventario.model.Rol;
import com.clinica.sistema.inventario.model.Usuario;
import com.clinica.sistema.inventario.service.UsuarioServicio;
import com.clinica.sistema.inventario.util.paginacion.PageRender;
import com.clinica.sistema.inventario.util.reportes.UsuarioExporterPDF;
import com.clinica.sistema.inventario.util.reportes.UsuarioFechaExporterPDF;
import com.clinica.sistema.inventario.util.reportes.UsuariosInactivosExporterPDF;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    // Inyectar el codificador de contraseñas (BCryptPasswordEncoder)
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public String listarUsuarios(@RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "buscar", required = false) String buscar,
                                 Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);

        Pageable pageRequest = PageRequest.of(page, 10);
        Page<Usuario> usuarios;

        // Si el parámetro 'buscar' no es null o vacío, realizar la búsqueda por nombre
        if (buscar != null && !buscar.trim().isEmpty()) {
            usuarios = usuarioServicio.buscarUsuariosPorNombre(buscar, pageRequest); // Llamar a la función de búsqueda por nombre
        } else {
            usuarios = usuarioServicio.findAll(pageRequest); // Obtener todos los usuarios si no hay búsqueda
        }

        PageRender<Usuario> pageRender = new PageRender<>("/usuarios", usuarios);

        model.addAttribute("titulo", "Listado de Usuarios");
        model.addAttribute("usuarios", usuarios.getContent());
        model.addAttribute("page", pageRender);
        model.addAttribute("buscar", buscar); // Añadir el parámetro de búsqueda al modelo

        return "UsuarioListar";
    }



    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String guardarUsuario(@Valid @ModelAttribute Usuario usuario,
                                 @RequestParam("role") String role, // Recibimos el parámetro del rol
                                 @RequestParam("fecha") String fecha, // Recibimos la fecha desde el formulario
                                 @RequestParam("password") String password, // Recibimos la contraseña del formulario
                                 BindingResult result,
                                 RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("error", "Por favor corrija los errores en el formulario");
            return "redirect:/usuarios";
        }

        try {

            // Convertir la fecha de String a LocalDate
            LocalDate fechaCreacion = LocalDate.parse(fecha);

            // Asignar el rol al usuario
            List<Rol> roles = new ArrayList<>();
            if ("ADMIN".equals(role)) {
                roles.add(new Rol("ROLE_ADMIN"));
            } else {
                roles.add(new Rol("ROLE_USER"));
            }
            usuario.setRoles(roles); // Asignamos los roles al usuario

            // Asignar el estado como ACTIVO
            usuario.setEstado(Estado.ACTIVO);

            // Encriptar la contraseña antes de asignarla
            usuario.setPassword(passwordEncoder.encode(password));

            // Asignar la fecha de creación
            usuario.setFecha(fechaCreacion);

            // Guardar el usuario
            usuarioServicio.save(usuario);
            flash.addFlashAttribute("success", "Usuario guardado con éxito");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar el usuario");
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/actualizar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String actualizar(@Valid @ModelAttribute Usuario usuario,
                             BindingResult result,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("error", "Por favor corrija los errores en el formulario");
            return "redirect:/usuarios";
        }

        try {
            Optional<Usuario> usuarioExistente = usuarioServicio.findById(usuario.getIdUsuario());
            if (usuarioExistente.isPresent()) {
                Usuario usuarioActual = usuarioExistente.get();
                usuarioActual.setNombre(usuario.getNombre());
                usuarioActual.setApellido(usuario.getApellido());
                usuarioActual.setEmail(usuario.getEmail());
                usuarioServicio.save(usuarioActual);
                flash.addFlashAttribute("success", "Usuario actualizado con éxito");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar el usuario");
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            // Buscar el usuario por ID
            Optional<Usuario> usuarioOptional = usuarioServicio.findById(id);

            // Verificar si el usuario existe
            if (usuarioOptional.isPresent()) {
                // Llamar al servicio para cambiar su estado a INACTIVO
                usuarioServicio.eliminarUsuarioLogicamente(id);
                flash.addFlashAttribute("success", "Usuario desactivado con éxito");
            } else {
                flash.addFlashAttribute("error", "El usuario no existe");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al desactivar el usuario");
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/reactivar/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String reactivarUsuario(@PathVariable Long id, RedirectAttributes flash) {
        try {
            Optional<Usuario> usuarioOptional = usuarioServicio.findById(id);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                // Cambiar el estado del usuario a ACTIVO
                usuario.setEstado(Estado.ACTIVO);
                usuarioServicio.save(usuario);
                flash.addFlashAttribute("success", "Usuario reactivado con éxito");
            } else {
                flash.addFlashAttribute("error", "El usuario no existe");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al reactivar el usuario");
        }
        return "redirect:/usuarios";
    }



    @ModelAttribute("roles")
    public List<String> getRoles() {
        return Arrays.asList("USER", "ADMIN");
    }


    // Método para exportar el reporte en PDF con Jakarta EE
    @GetMapping("/exportar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void exportarUsuariosEnPDF(HttpServletResponse response) throws DocumentException, IOException {
        // Obtener todos los usuarios
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();

        // Crear el Exportador de PDF
        UsuarioExporterPDF exporter = new UsuarioExporterPDF(usuarios);

        // Configurar la respuesta HTTP para que sea un archivo PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios_report.pdf");

        // Exportar el PDF
        exporter.exportar(response);
    }

    // Método para exportar el reporte de usuarios por fecha en PDF con Jakarta EE
    @GetMapping("/exportarPorFecha")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void exportarReportePorFecha(HttpServletResponse response) throws DocumentException, IOException {
        // Obtener todos los usuarios
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();

        // Crear el exportador de PDF para el reporte por fecha
        UsuarioFechaExporterPDF exporter = new UsuarioFechaExporterPDF();

        // Configurar la respuesta HTTP para que sea un archivo PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios_por_fecha.pdf");

        // Exportar el PDF con la lista de usuarios
        exporter.generarReportePorFecha(usuarios, response);
    }

    @GetMapping("/exportarInactivos")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void exportarUsuariosInactivosEnPDF(HttpServletResponse response) throws IOException {
        // Obtener todos los usuarios
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();

        // Crear el exportador de PDF
        UsuariosInactivosExporterPDF exporter = new UsuariosInactivosExporterPDF();

        // Exportar el PDF
        exporter.exportar(usuarios, response);
    }

}

