package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.model.Categoria;
import com.clinica.sistema.inventario.model.Proveedor;
import com.clinica.sistema.inventario.service.ProveedorServicio;
import com.clinica.sistema.inventario.util.paginacion.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class ProveedorController {

    @Autowired
    private ProveedorServicio proveedorServicio;


    @GetMapping("/proveedor")
    public String listarProveedores(@RequestParam(name = "page", defaultValue = "0")int page, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("isAdmin", userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));
        }
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Proveedor> proveedores = proveedorServicio.findAll(pageRequest);
        PageRender<Proveedor> pageRender = new PageRender<>("/proveedor", proveedores);

        model.addAttribute("proveedor", proveedores.getContent());
        model.addAttribute("page", pageRender);

        return "ProveedorListar";
    }

    @PostMapping("/proveedor/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor, RedirectAttributes flash){
        try {
            proveedorServicio.save(proveedor);
            flash.addFlashAttribute("success", "Categoría guardada con éxito");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar la categoría");
        }
        return "redirect:/proveedor";
    }

    @PostMapping("/proveedor/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Proveedor proveedor, RedirectAttributes flash){
        try {
            Proveedor proveedorExistente = proveedorServicio.findById(id)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

            proveedorExistente.setNombre(proveedor.getNombre());
            proveedorExistente.setDireccion(proveedor.getDireccion());
            proveedorExistente.setCorreo(proveedor.getCorreo());

            proveedorServicio.save(proveedorExistente);
            flash.addFlashAttribute("success", "Proveedor actualizado con éxito");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar el Proveedor");
        }
        return "redirect:/proveedor";
    }

    @PostMapping("/proveedor/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            if (id > 0) {
                proveedorServicio.delete(id);
                flash.addFlashAttribute("success", "Proveedor eliminado con éxito");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar el proveedor");
        }
        return "redirect:/proveedor";
    }
}
