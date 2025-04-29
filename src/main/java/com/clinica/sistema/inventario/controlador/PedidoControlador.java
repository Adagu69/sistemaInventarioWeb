package com.clinica.sistema.inventario.controlador;


import com.clinica.sistema.inventario.model.*;
import com.clinica.sistema.inventario.service.PedidoServicio;
import com.clinica.sistema.inventario.service.ProductoServicio;
import com.clinica.sistema.inventario.service.ProveedorServicio;
import com.clinica.sistema.inventario.service.UsuarioServicio;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/pedido")
public class PedidoControlador {

    @Autowired
    private PedidoServicio pedidoServicio;

    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ProveedorServicio proveedorServicio;

    @GetMapping
    public String mostrarPaginaPedidos(Model model, Principal principal) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = false;
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                isAdmin = true;
            }
            model.addAttribute("isAdmin", isAdmin);

        if (principal == null) {
            return "redirect:/login";
        }
        String emailUsuario = principal.getName();
        Usuario usuario = usuarioServicio.findByEmail(emailUsuario);

        model.addAttribute("pedido", new Pedido());
        model.addAttribute("proveedores", proveedorServicio.findAll());

        if (usuario != null && usuario.hasRole("ROLE_ADMIN")) {
            model.addAttribute("pedidos", pedidoServicio.findAll());
        } else if (usuario != null) {
            model.addAttribute("pedidos", pedidoServicio.findByUsuario(usuario.getIdUsuario()));
        } else {
            return "redirect:/login";
        }
        return "PedidoListar";
    }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/guardar")
    public String guardarPedido(@Valid @ModelAttribute("pedido") Pedido pedido,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes, Model model, Principal principal) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("proveedores", proveedorServicio.findAll());
            model.addAttribute("pedidos", pedidoServicio.findAll());
            return "PedidoListar";
        }

        String emailUsuario = principal.getName();
        Usuario usuario = usuarioServicio.findByEmail(emailUsuario);
        if (usuario == null) {
            bindingResult.rejectValue("usuario", "error.usuario", "Usuario no válido");
            model.addAttribute("proveedores", proveedorServicio.findAll());
            model.addAttribute("pedidos", pedidoServicio.findAll());
            return "PedidoListar";
        }

        pedido.setUsuario(usuario);

        if (pedido.getProveedor() != null && pedido.getProveedor().getIdProveedor() != null) {
            Proveedor proveedor = proveedorServicio.findOne(pedido.getProveedor().getIdProveedor() );
            if (proveedor == null) {
                bindingResult.rejectValue("proveedor", "error.proveedor", "Proveedor no válido");
                model.addAttribute("proveedores", proveedorServicio.findAll());
                model.addAttribute("pedidos", pedidoServicio.findAll());
                return "PedidoListar";
            }
            pedido.setProveedor(proveedor);
        } else {
            bindingResult.rejectValue("proveedor", "error.proveedor", "Proveedor no seleccionado");
            model.addAttribute("proveedores", proveedorServicio.findAll());
            model.addAttribute("solicitudes", pedidoServicio.findAll());
            return "PedidoListar";
        }

        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDateTime.now());
        }

        pedidoServicio.save(pedido);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido guardado exitosamente");

        return "redirect:/pedido";


    }
}
