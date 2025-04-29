package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.model.Producto;
import com.clinica.sistema.inventario.model.Solicitud;
import com.clinica.sistema.inventario.model.Usuario;
import com.clinica.sistema.inventario.service.ISolicitudServicio;
import com.clinica.sistema.inventario.service.ProductoServicio;
import com.clinica.sistema.inventario.service.SolicitudServicio;
import com.clinica.sistema.inventario.service.UsuarioServicio;
import com.clinica.sistema.inventario.util.paginacion.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudControlador {

    @Autowired
    private SolicitudServicio solicitudServicio;

    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping
    public String mostrarPaginaSolicitudes(Model model, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }

            String emailUsuario = principal.getName();
            Usuario usuario = usuarioServicio.findByEmail(emailUsuario);

            if (usuario == null) {
                return "redirect:/login";
            }

            // Verifica si el usuario es administrador
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);

            // Agrega los datos al modelo
            model.addAttribute("solicitud", new Solicitud());
            model.addAttribute("productos", productoServicio.findAll());

            if (isAdmin) {
                model.addAttribute("solicitudes", solicitudServicio.findAll());
            } else {
                model.addAttribute("solicitudes", solicitudServicio.findByUsuario(usuario.getIdUsuario()));
            }

            return "Solicitud";
        } catch (Exception e) {
            e.printStackTrace(); // Imprime la excepción en los logs
            throw new RuntimeException("Error al cargar la página de solicitudes", e);
        }
    }

    @PostMapping("/guardar")
    public String guardarSolicitud(@Valid @ModelAttribute("solicitud") Solicitud solicitud,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model,
                                   Principal principal) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("productos", productoServicio.findAll());
            model.addAttribute("solicitudes", solicitudServicio.findAll());
            return "Solicitud";
        }

        String emailUsuario = principal.getName();
        Usuario usuario = usuarioServicio.findByEmail(emailUsuario);
        if (usuario == null) {
            bindingResult.rejectValue("usuario", "error.usuario", "Usuario no válido");
            model.addAttribute("productos", productoServicio.findAll());
            model.addAttribute("solicitudes", solicitudServicio.findAll());
            return "Solicitud";
        }

        solicitud.setUsuario(usuario);

        if (solicitud.getProducto() != null && solicitud.getProducto().getIdProducto() != null) {
            Producto producto = productoServicio.findOne(solicitud.getProducto().getIdProducto());
            if (producto == null) {
                bindingResult.rejectValue("producto", "error.producto", "Producto no válido");
                model.addAttribute("productos", productoServicio.findAll());
                model.addAttribute("solicitudes", solicitudServicio.findAll());
                return "Solicitud";
            }
            solicitud.setProducto(producto);
        } else {
            bindingResult.rejectValue("producto", "error.producto", "Producto no seleccionado");
            model.addAttribute("productos", productoServicio.findAll());
            model.addAttribute("solicitudes", solicitudServicio.findAll());
            return "Solicitud";
        }

        if (solicitud.getFecha() == null) {
            solicitud.setFecha(LocalDateTime.now());
        }

        solicitudServicio.save(solicitud);
        redirectAttributes.addFlashAttribute("mensaje", "Solicitud guardada exitosamente");

        return "redirect:/solicitudes";
    }

    @GetMapping("/testsave")
    public String testSave() {
        Solicitud solicitud = new Solicitud();
        solicitud.setCantidad(10);
        solicitud.setFecha(LocalDateTime.now());
        Producto producto = productoServicio.findOne(1L);
        Usuario usuario = usuarioServicio.findOne(1L);
        solicitud.setProducto(producto);
        solicitud.setUsuario(usuario);
        solicitudServicio.save(solicitud);
        return "redirect:/solicitudes";
    }
}

