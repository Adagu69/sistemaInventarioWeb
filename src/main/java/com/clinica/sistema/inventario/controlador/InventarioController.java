package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.model.Inventario;
import com.clinica.sistema.inventario.repository.InventarioRepositorio;
import com.clinica.sistema.inventario.service.InventarioServicio;
import com.clinica.sistema.inventario.util.reportes.InventarioExporterPDF;
import com.clinica.sistema.inventario.util.reportes.InventarioPromedioExporterPDF;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class InventarioController {

    @Autowired
    private InventarioRepositorio inventarioRepositorio;
    @Autowired
    private InventarioServicio inventarioServicio;

    // Mostrar inventarios con productos asociados
    @GetMapping("/inventario")
    public String mostrarInventario(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = false;
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                isAdmin = true;
            }
            model.addAttribute("isAdmin", isAdmin);
            // Obtener todos los inventarios de la base de datos
            List<Inventario> inventarioList = inventarioRepositorio.findAll();
            model.addAttribute("inventarios", inventarioList); // Pasar la lista de inventarios a la vista
            return "inventario"; // Redirigir a la vista inventario.html
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/inventario/precio/{idProducto}")
    @ResponseBody
    public Map<String, Double> obtenerPrecioInventario(@PathVariable Long idProducto) {
        Inventario inventario = inventarioServicio.findByProductoIdProducto(idProducto);
        if (inventario == null) {
            throw new RuntimeException("Inventario no encontrado");
        }
        return Collections.singletonMap("precio", inventario.getPrecio());
    }


    // Buscar inventarios por nombre del producto
    @GetMapping("/inventario/buscar")
    public String buscarInventarios(@RequestParam("nombreProducto") String nombreProducto, Model model) {
        List<Inventario> inventarios = inventarioServicio.findByProductoNombreContainingIgnoreCase(nombreProducto);
        model.addAttribute("inventarios", inventarios); // Pasar los inventarios filtrados a la vista
        return "inventario"; // Redirigir a la vista inventario.html
    }

    // Generar el reporte PDF
    @GetMapping("/inventario/exportar")
    public void exportarInventario(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=inventario_report.pdf";
        response.setHeader(headerKey, headerValue);

        List<Inventario> inventarios = inventarioRepositorio.findAll();
        InventarioExporterPDF exporter = new InventarioExporterPDF(inventarios);
        exporter.exportar(response);}

    // Generar reporte de máximos, mínimos y promedio
    @GetMapping("/inventario/promedio/exportar")
    public void exportarPromedio(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=inventario_promedios_report.pdf";
        response.setHeader(headerKey, headerValue);

        List<Inventario> inventarios = inventarioRepositorio.findAll();
        InventarioPromedioExporterPDF exporter = new InventarioPromedioExporterPDF(inventarios);
        exporter.exportar(response);
    }

}