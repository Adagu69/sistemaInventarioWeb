package com.clinica.sistema.inventario.controlador;

import com.clinica.sistema.inventario.model.Categoria;
import com.clinica.sistema.inventario.model.Producto;
import com.clinica.sistema.inventario.model.Proveedor;
import com.clinica.sistema.inventario.repository.ProductoRepositorio;
import com.clinica.sistema.inventario.service.CategoriaServicio;
import com.clinica.sistema.inventario.service.ProductoServicio;
import com.clinica.sistema.inventario.service.ProveedorServicio;
import com.clinica.sistema.inventario.util.paginacion.PageRender;
import com.clinica.sistema.inventario.util.reportes.ProductoExporterPDF;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class ProductoController {
    @Autowired
    private ProductoServicio productoServicio;

    @Autowired
    private CategoriaServicio categoriaServicio;

    @Autowired
    private ProveedorServicio proveedorServicio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @GetMapping("/producto")
    public String listarProductos(@RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name ="buscarProducto", required = false) String buscar ,
                                  Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null &&
                    authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            model.addAttribute("isAdmin", isAdmin);

            // Configuración de paginación
            Pageable pageRequest = PageRequest.of(page, 10);
            Page<Producto> productosPage = productoServicio.findAll(pageRequest);

            if (buscar != null && !buscar.trim().isEmpty()) {
                productosPage = productoServicio.buscarUsuariosPorNombre(buscar, pageRequest);
            }else {
                productosPage = productoServicio.findAll(pageRequest);
            }

            // Para el formulario de nuevo producto
            model.addAttribute("producto", new Producto());

            // Verificar que los productos y sus relaciones no sean nulos
            List<Producto> productos = productosPage.getContent().stream()
                    .filter(p -> p != null && p.getCategoria() != null && p.getProveedor() != null)
                    .collect(Collectors.toList());

            // Agregar atributos al modelo
            model.addAttribute("productos", productos);
            model.addAttribute("categorias", categoriaServicio.listarCategorias());
            model.addAttribute("proveedores", proveedorServicio.listarProveedores());
            model.addAttribute("page", new PageRender<>("/producto", productosPage));
            model.addAttribute("buscarProducto", buscar); // Mantener el valor del campo de búsqueda
            return "ProductoListar";

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la página: " + e.getMessage());
            return "error";
        }
    }


    @PostMapping("/producto/guardar")
    public String guardar(@ModelAttribute("producto") Producto producto,
                          RedirectAttributes flash,
                          @RequestParam("categoria") Long categoriaId,
                          @RequestParam("proveedor") Long proveedorId) {
        try {
            Categoria categoria = categoriaServicio.findById(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            Proveedor proveedor = proveedorServicio.findById(proveedorId)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

            producto.setCategoria(categoria);
            producto.setProveedor(proveedor);
            productoServicio.save(producto);
            flash.addFlashAttribute("success", "Producto guardado con éxito");
        } catch(Exception e) {
            flash.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
        }
        return "redirect:/producto";
    }

    @PostMapping("/producto/actualizar")
    public String actualizar(@ModelAttribute Producto producto,
                             RedirectAttributes flash,
                             @RequestParam("categoria") Long categoriaId,
                             @RequestParam("proveedor") Long proveedorId) {
        try {
            Categoria categoria = categoriaServicio.findById(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            Proveedor proveedor = proveedorServicio.findById(proveedorId)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

            Optional<Producto> productoExistente = productoServicio.findById(producto.getIdProducto());
            if (productoExistente.isPresent()) {
                Producto productoUpdate = productoExistente.get();
                productoUpdate.setNombre(producto.getNombre());
                productoUpdate.setDescripcion(producto.getDescripcion());
                productoUpdate.setCategoria(categoria);
                productoUpdate.setProveedor(proveedor);
                productoServicio.save(productoUpdate);
                flash.addFlashAttribute("success", "Producto actualizado con éxito");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar el producto: " + e.getMessage());
        }
        return "redirect:/producto";
    }

    @PostMapping("/producto/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            if (id > 0) {
                productoServicio.delete(id);
                flash.addFlashAttribute("success", "Producto eliminado con éxito");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/producto";
    }

    // Generar el reporte PDF para productos
    @GetMapping("/productos/exportar")
    public void exportarProductos(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=productos_report.pdf";
        response.setHeader(headerKey, headerValue);

        List<Producto> productos = productoRepositorio.findAll();
        ProductoExporterPDF exporter = new ProductoExporterPDF(productos);
        exporter.exportar(response);
    }


}
