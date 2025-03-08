package com.clinica.sistema.inventario.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
public class ConsultaControlador {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/consultas")
    public String mostrarConsultas(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = false;
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                isAdmin = true;
            }
            model.addAttribute("isAdmin", isAdmin);
        // Opciones para el select
        List<String> opciones = List.of("Proveedores", "Productos");
        model.addAttribute("opciones", opciones);
        return "Consultas"; // Nombre del archivo HTML (sin extensión)
    }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Método para listar los datos con JOIN
    @GetMapping("/consultas/listar")
    @ResponseBody
    public List<Map<String, Object>> listarDatos() {
        // Consulta SQL con JOIN para obtener productos y proveedores
        String sql = "SELECT p.id_producto, p.descripcion, p.nombre AS producto_nombre, " +
                "p.categoria_id, p.proveedor_id, pr.correo, pr.direccion, pr.nombre AS proveedor_nombre " +
                "FROM productos p " +
                "JOIN proveedores pr ON p.proveedor_id = pr.id_proveedor";

        // Ejecutar la consulta y devolver los resultados
        return jdbcTemplate.queryForList(sql);
    }
    // Nueva ruta para agregar productos o proveedores
    @GetMapping("/consultas/agregar")
    @ResponseBody
    public List<Map<String, Object>> agregarDatos(@RequestParam String opcion) {
        String sql = "";

        // Determinamos qué consulta hacer según la opción seleccionada
        if (opcion.equals("Proveedores")) {
            sql = "SELECT id_proveedor, correo, direccion, nombre FROM proveedores";
        } else if (opcion.equals("Productos")) {
            sql = "SELECT p.id_producto, p.descripcion, p.nombre AS nombre, p.categoria_id, p.proveedor_id, pr.nombre AS proveedor_nombre " +
                    "FROM productos p " +
                    "JOIN proveedores pr ON p.proveedor_id = pr.id_proveedor";
        }

        // Ejecutamos la consulta correspondiente y devolvemos los resultados
        return jdbcTemplate.queryForList(sql);
    }
}
