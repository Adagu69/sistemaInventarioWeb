// Archivo: UsuarioControlador.java
package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.controlador.dto.UsuarioRegistroDTO;
import com.clinica.sistema.inventario.model.Categoria;
import com.clinica.sistema.inventario.model.Usuario;
import com.clinica.sistema.inventario.service.UsuarioServicio;
import com.clinica.sistema.inventario.util.paginacion.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public String listarUsuarios(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);

        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Usuario> usuarios = usuarioServicio.findAll(pageRequest);
        PageRender<Usuario> pageRender = new PageRender<>("/usuarios", usuarios);

        model.addAttribute("titulo", "Listado de Usuarios");
        model.addAttribute("usuarios", usuarios.getContent());
        model.addAttribute("page", pageRender);

        return "UsuarioListar";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String guardarUsuario(@Valid @ModelAttribute Usuario usuario,
                                 BindingResult result,
                                 RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("error", "Por favor corrija los errores en el formulario");
            return "redirect:/usuarios";
        }

        try {
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
            if (usuarioServicio.findById(id).isPresent()) {
                usuarioServicio.delete(id);
                flash.addFlashAttribute("success", "Usuario eliminado con éxito");
            } else {
                flash.addFlashAttribute("error", "El usuario no existe");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar el usuario");
        }
        return "redirect:/usuarios";
    }

    @ModelAttribute("roles")
    public List<String> getRoles() {
        return Arrays.asList("USER", "ADMIN");
    }

}

