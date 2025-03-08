package com.clinica.sistema.inventario.controlador;


import com.clinica.sistema.inventario.model.Inventario;
import com.clinica.sistema.inventario.model.Movimiento;
import com.clinica.sistema.inventario.model.Producto;
import com.clinica.sistema.inventario.model.Usuario;
import com.clinica.sistema.inventario.repository.InventarioRepositorio;
import com.clinica.sistema.inventario.service.InventarioServicio;
import com.clinica.sistema.inventario.service.MovimientoServicio;
import com.clinica.sistema.inventario.service.ProductoServicio;
import com.clinica.sistema.inventario.service.UsuarioServicio;
import com.clinica.sistema.inventario.util.reportes.MovimientoExporterPDF;
import com.clinica.sistema.inventario.util.reportes.MovimientoUtilidadExporterPDF;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/movimientos")
public class MovimientoControlador {

    @Autowired
    private MovimientoServicio movimientoServicio;

    @Autowired
    private ProductoServicio productoService;

    @Autowired
    private InventarioServicio inventarioServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private MovimientoExporterPDF movimientoExporterPDF;

    @Autowired
    private MovimientoUtilidadExporterPDF movimientoUtilidadExporterPDF;

    @GetMapping
    public String mostrarRegistroMovimientos(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = false;
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                isAdmin = true;
            }
            model.addAttribute("isAdmin", isAdmin);

            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("movimientoEntrada", new Movimiento());
            model.addAttribute("movimientoSalida", new Movimiento());
            model.addAttribute("movimientos", movimientoServicio.findAll());
            return "MovimientoListar";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/entrada")
    public String registrarEntrada(@Valid @ModelAttribute("movimientoEntrada") Movimiento movimiento,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Por favor, corrige los errores en el formulario.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movimientoEntrada", bindingResult);
            return "redirect:/movimientos";
        }
        try {
            movimiento.setTipo("ENTRADA");
            movimiento.setFecha(new Timestamp(System.currentTimeMillis()));
            movimiento.setEstado("ACTIVO");
            movimiento.setTotal(movimiento.getCantidad() * movimiento.getPrecio());

            movimientoServicio.save(movimiento);

            // Actualizar inventario
            Inventario inventario = inventarioServicio.findByProductoIdProducto(movimiento.getProducto().getIdProducto());
            if (inventario == null) {
                inventario = new Inventario();
                inventario.setProducto(movimiento.getProducto());
            }
            inventario.setCantidad(inventario.getCantidad() + movimiento.getCantidad());
            inventario.setPrecio(movimiento.getPrecio());
            inventario.setEstado("ACTIVO");
            inventario.setFecha(String.valueOf(movimiento.getFecha()));
            inventarioServicio.save(inventario);

            redirectAttributes.addFlashAttribute("mensaje", "Entrada registrada correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar entrada: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/movimientos";
    }


    @PostMapping("/salida")
    public String registrarSalida(@Valid @ModelAttribute("movimientoSalida") Movimiento movimiento,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Por favor, corrige los errores en el formulario.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movimientoSalida", bindingResult);
            return "redirect:/movimientos";
        }
        try {
            movimiento.setTipo("SALIDA");
            movimiento.setFecha(new Timestamp(System.currentTimeMillis()));
            movimiento.setEstado("ACTIVO");
            movimiento.setTotal(movimiento.getCantidad() * movimiento.getPrecio());

            movimientoServicio.save(movimiento);


            // Actualizar inventario
            Inventario inventario = inventarioServicio.findByProductoIdProducto(movimiento.getProducto().getIdProducto());
            if (inventario != null) {
                inventario.setCantidad(inventario.getCantidad() - movimiento.getCantidad());
                inventario.setPrecio(movimiento.getPrecio());
                inventario.setEstado("ACTIVO");
                inventario.setFecha(String.valueOf(movimiento.getFecha()));
                inventarioServicio.save(inventario);
            }

            redirectAttributes.addFlashAttribute("mensaje", "Salida registrada correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al registrar salida: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/movimientos";
    }

    @GetMapping("/reporte")
    public void generarReporte(HttpServletResponse response) {
        try {
            // Configurar la respuesta para que sea un archivo PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"reporte_movimientos.pdf\"");

            // Obtener los movimientos de entrada y salida
            List<Movimiento> entradas = movimientoServicio.findByTipo("ENTRADA");
            List<Movimiento> salidas = movimientoServicio.findByTipo("SALIDA");

            // Llamar al servicio para exportar a PDF
            movimientoExporterPDF.exportar(entradas, salidas, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método adicional para generar un reporte de ingreso/utilidad
    @GetMapping("/reporteUtilidad")
    public void generarReporteUtilidad(HttpServletResponse response) {
        try {
            // Configurar la respuesta para que sea un archivo PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"reporte_utilidad.pdf\"");

            // Obtener todos los movimientos de entrada y salida
            List<Movimiento> entradas = movimientoServicio.findByTipo("ENTRADA");
            List<Movimiento> salidas = movimientoServicio.findByTipo("SALIDA");

            // Llamar al método de exportación de la clase MovimientoUtilidadExporterPDF
            movimientoUtilidadExporterPDF.exportar(entradas, salidas, response);
        } catch (Exception e) {
            e.printStackTrace();  // Mejorar manejo de errores en producción
        }
    }

}
