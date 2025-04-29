package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.controlador.dto.UsuarioRegistroDTO;
import com.clinica.sistema.inventario.model.Area;
import com.clinica.sistema.inventario.service.AreaServicio;
import com.clinica.sistema.inventario.service.IUsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/registro")
public class RegistroUsuarioControlador {

    private IUsuarioServicio usuarioServicio;
    private AreaServicio areaServicio;

    @Autowired
    public RegistroUsuarioControlador(IUsuarioServicio usuarioServicio, AreaServicio areaServicio) {
        super();
        this.usuarioServicio = usuarioServicio;
        this.areaServicio = areaServicio;
    }

    @ModelAttribute("usuario")
    public UsuarioRegistroDTO retornarNuevoUsuarioRegistroDTO() {
        return new UsuarioRegistroDTO();
    }

    @GetMapping
    public String mostrarFormularioDeRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDTO());
        model.addAttribute("areas", areaServicio.listarAreasActivas());
        return "registro";
    }

    @PostMapping
    public String registrarCuentaDeUsuario(@Valid @ModelAttribute("usuario") UsuarioRegistroDTO registroDTO,
                                           BindingResult result,
                                           @RequestParam("areaId") Long areaId,
                                           Model model){
        if (result.hasErrors()) {
            model.addAttribute("areas", areaServicio.listarAreasActivas());
            return "registro"; // Vuelve al formulario si hay errores
        }

        usuarioServicio.guardar(registroDTO, areaId);
        model.addAttribute("exito", "Usuario registrado exitosamente");
        return "redirect:/registro?exito";
    }
}
