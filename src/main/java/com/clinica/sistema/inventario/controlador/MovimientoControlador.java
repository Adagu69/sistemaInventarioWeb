package com.clinica.sistema.inventario.controlador;


import com.clinica.sistema.inventario.controlador.dto.MovimientoDTO;
import com.clinica.sistema.inventario.model.*;
import com.clinica.sistema.inventario.repository.InventarioRepositorio;
import com.clinica.sistema.inventario.service.*;
import com.clinica.sistema.inventario.util.RedondeoUtil;
import com.clinica.sistema.inventario.util.paginacion.PageRender;
import com.clinica.sistema.inventario.util.reportes.MovimientoExporterPDF;
import com.clinica.sistema.inventario.util.reportes.MovimientoUtilidadExporterPDF;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private AreaServicio areaServicio;

    @Autowired
    private MovimientoExporterPDF movimientoExporterPDF;

    @Autowired
    private MovimientoUtilidadExporterPDF movimientoUtilidadExporterPDF;

    private boolean isAdmin(Principal principal) {
        if (principal == null) return false;
        Authentication authentication = (Authentication) principal;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Usuario obtenerUsuarioActual(Principal principal) {
        if (principal == null) return null;
        return usuarioServicio.findByEmail(principal.getName());
    }



    @GetMapping
    public String mostrarRegistroMovimientos(@RequestParam(name = "page", defaultValue = "0") int page,
                                             @RequestParam(required = false) String exito,
                                                @RequestParam(required = false) String error,
                                             Model model,
                                             Principal principal) {
        try {
            boolean isAdmin = isAdmin(principal);
            Usuario usuario = obtenerUsuarioActual(principal);
            Area areaUsuario = (!isAdmin && usuario != null) ? usuario.getArea() : null;

            // Validar que usuarios no-admin tengan área asignada
            if (!isAdmin && areaUsuario == null) {
                throw new IllegalStateException("El usuario no tiene un área asignada. Contacte al administrador.");
            }

            Pageable pageable = PageRequest.of(page, 10);
            Page<Movimiento> movimientosPage = movimientoServicio.findAll(pageable);


            model.addAttribute("productos", productoService.findAll());
            model.addAttribute("movimientoDTO", new MovimientoDTO());
            model.addAttribute("areas", areaServicio.listarAreasActivas());
            model.addAttribute("areaUsuario", areaUsuario);
            model.addAttribute("movimientos", movimientoServicio.findAll());
            model.addAttribute("movimientos", movimientosPage.getContent());
            model.addAttribute("page", new PageRender<>("/movimientos", movimientosPage));
            model.addAttribute("isAdmin", isAdmin);
            manejarMensajes(model, exito, error);

            return "MovimientoListar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la página: " + e.getMessage());
            return "MovimientoListar";
        }
    }

    @PostMapping("/registrar")
    public String registrarMovimiento(@Valid @ModelAttribute MovimientoDTO movimientoDTO,
                                      Principal principal,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.movimientoDTO", bindingResult);
            redirectAttributes.addFlashAttribute("movimientoDTO", movimientoDTO);
            return "redirect:/movimientos";
        }

        try {
            boolean isAdmin = isAdmin(principal);
            movimientoDTO.validarArea(isAdmin);

            Movimiento movimiento = crearMovimientoDesdeDTO(movimientoDTO, principal);
            movimientoServicio.registrarMovimiento(movimiento, principal.getName(), movimientoDTO);

            redirectAttributes.addFlashAttribute("mensaje", "Movimiento registrado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            manejarErrorRegistro(redirectAttributes, e);
        }
        return "redirect:/movimientos";
    }

    // Métodos auxiliares privados
    private void cargarDatosVista(Model model, boolean isAdmin, Area areaUsuario) {
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("movimientoDTO", new MovimientoDTO());
        model.addAttribute("areas", areaServicio.listarAreasActivas());
        model.addAttribute("areaUsuario", areaUsuario);
        model.addAttribute("movimientos", movimientoServicio.findAll());
        model.addAttribute("isAdmin", isAdmin);
    }

    private void manejarMensajes(Model model, String exito, String error) {
        if (exito != null) {
            model.addAttribute("mensaje", exito);
            model.addAttribute("tipoMensaje", "success");
        }
        if (error != null) {
            model.addAttribute("mensaje", error);
            model.addAttribute("tipoMensaje", "error");
        }
    }

    private void manejarErroresValidacion(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", "Por favor, corrige los errores en el formulario.");
        redirectAttributes.addFlashAttribute("tipoMensaje", "error");
    }

    private Movimiento crearMovimientoDesdeDTO(MovimientoDTO movimientoDTO, Principal principal) {
        Usuario usuario = obtenerUsuarioActual(principal);
        boolean isAdmin = isAdmin(principal);

        Producto producto = productoService.findById(movimientoDTO.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(movimientoDTO.getTipo());
        movimiento.setCantidad(movimientoDTO.getCantidad());
        movimiento.setPrecio(calcularPrecioMovimiento(movimientoDTO));
        movimiento.setFecha(movimientoDTO.getFecha() != null ? movimientoDTO.getFecha() : LocalDateTime.now());
//        movimiento.setPrecio(calcularPrecioMovimiento(movimientoDTO));
        movimiento.setMotivo(movimientoDTO.getMotivo());
        movimiento.setEstado("ACTIVO");
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setArea(determinarArea(usuario, movimientoDTO.getAreaId(), isAdmin));

        return movimiento;
    }

    private double calcularPrecioMovimiento(MovimientoDTO movimientoDTO) {
        if ("SALIDA".equalsIgnoreCase(movimientoDTO.getTipo())) {
            Inventario inventario = inventarioServicio.findByProductoIdProducto(movimientoDTO.getProductoId());
            if (inventario == null) {
                throw new RuntimeException("No se encontró inventario para el producto seleccionado");
            }
            return RedondeoUtil.redondear(inventario.getPrecio()); // aplica redondeo
        } else {
            return RedondeoUtil.redondear(movimientoDTO.getPrecio()); // redondear entrada también
        }
    }

    private Area determinarArea(Usuario usuario, Long areaId, boolean isAdmin) {
        if (isAdmin) {
            return areaServicio.obtenerAreaPorId(areaId);
        }
        return Optional.ofNullable(usuario.getArea())
                .orElseThrow(() -> new RuntimeException("El usuario no tiene un área asignada"));
    }

    private void manejarErrorRegistro(RedirectAttributes redirectAttributes, Exception e) {
        redirectAttributes.addFlashAttribute("mensaje", "Error al registrar movimiento: " + e.getMessage());
        redirectAttributes.addFlashAttribute("tipoMensaje", "error");
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



