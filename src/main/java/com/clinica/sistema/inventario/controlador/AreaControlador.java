package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.model.Area;
import com.clinica.sistema.inventario.model.Producto;
import com.clinica.sistema.inventario.repository.AreaRepositorio;
import com.clinica.sistema.inventario.service.AreaServicio;
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
public class AreaControlador {

    @Autowired
    private AreaServicio areaServicio;

    @GetMapping("/area")
    public String listarCategoria(@RequestParam(name= "page", defaultValue = "0")int page,
                                  Model model){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null &&
                    authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            model.addAttribute("isAdmin", isAdmin);

            Pageable pageRequest = PageRequest.of(page, 10);
            Page<Area> areas = areaServicio.findAll(pageRequest);
            PageRender<Area> pageRender = new PageRender<>("/area", areas);

            model.addAttribute("area", new Area());

            //Verificar que las areas no sean nulas
            if(areas == null || areas.getContent().isEmpty()){
                model.addAttribute("error", "No se encontraron áreas");
                return "AreaListar";
            }

            model.addAttribute("areas", areas);
            model.addAttribute("page", pageRender);

            return "AreaListar";
        }catch (Exception e){
            model.addAttribute("error", "Error al cargar la página: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/area/guardar")
    public String guardar(@ModelAttribute Area area, RedirectAttributes flash){
        try{
            areaServicio.save(area);
            flash.addFlashAttribute("success", "Área guardada con éxito");
        }catch (Exception e){
            flash.addFlashAttribute("error", "Error al guardar el área");
        }
        return "redirect:/area";
    }

    @PostMapping("/area/actualizar")
    public String actualizar(@ModelAttribute Area area, RedirectAttributes flash){
        try{
            Optional<Area> areaExistente = areaServicio.findById(area.getIdArea());
            if(areaExistente.isPresent()){
                areaExistente.get().setNombre(area.getNombre());
                areaServicio.save(areaExistente.orElse(null));
                flash.addFlashAttribute("success", "Área actualizada con éxito");
            }
        }catch (Exception e){
            flash.addFlashAttribute("error", "Error al actualizar el área");
        }
        return "redirect:/area";
    }

    @PostMapping("/area/eliminar/{idArea}")
    public String eliminar(@PathVariable Long idArea, RedirectAttributes flash){
        if(idArea > 0){
            try{
                areaServicio.delete(idArea);
                flash.addFlashAttribute("success", "Área eliminada con éxito");
            }catch (Exception e){
                flash.addFlashAttribute("error", "Error al eliminar el área");
            }
        }
        return "redirect:/area";
    }





}
