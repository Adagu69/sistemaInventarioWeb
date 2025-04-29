package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.model.Categoria;
import com.clinica.sistema.inventario.repository.CategoriaRepositorio;
import com.clinica.sistema.inventario.service.CategoriaServicio;
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
public class CategoriaController {

    @Autowired
    private CategoriaServicio categoriaServicio;
    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @GetMapping("/categoria")
    public String listarCategorias(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("isAdmin", userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));
        }
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Categoria> categorias = categoriaServicio.findAll(pageRequest);
        PageRender<Categoria> pageRender = new PageRender<>("/categoria", categorias);

        model.addAttribute("categoria", categorias.getContent());
        model.addAttribute("page", pageRender);
        return "pages/CategoriaListar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, RedirectAttributes flash) {
        try {
            categoriaServicio.save(categoria);
            flash.addFlashAttribute("success", "Categoría guardada con éxito");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar la categoría");
        }
        return "redirect:/categoria";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Categoria categoria, RedirectAttributes flash) {
        try {
            Optional<Categoria> categoriaExistente = categoriaServicio.findById(categoria.getId());
            if (categoriaExistente.isPresent()) {
                categoriaExistente.get().setNombre(categoria.getNombre());
                categoriaServicio.save(categoriaExistente.orElse(null));
                flash.addFlashAttribute("success", "Categoría actualizada con éxito");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar la categoría");
        }
        return "redirect:/categoria";
    }


    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        if (id > 0) {
            try {
                categoriaServicio.delete(id);
                flash.addFlashAttribute("success", "Categoría eliminada con éxito");
            } catch (Exception e) {
                flash.addFlashAttribute("error", "Error al eliminar la categoría");
            }
        }
        return "redirect:/categoria";  // Cambiar a /categoria
    }
}
