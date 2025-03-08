package com.clinica.sistema.inventario.util.reportes;

import com.clinica.sistema.inventario.model.Inventario;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class InventarioPromedioExporterPDF {

    private List<Inventario> inventarios;

    public InventarioPromedioExporterPDF(List<Inventario> inventarios) {
        this.inventarios = inventarios;
    }

    private double calcularPromedio() {
        return inventarios.stream()
                .mapToDouble(inventario -> inventario.getCantidad() * inventario.getPrecio())
                .average()
                .orElse(0.0);
    }

    private double calcularMaximo() {
        return inventarios.stream()
                .mapToDouble(inventario -> inventario.getCantidad() * inventario.getPrecio())
                .max()
                .orElse(0.0);
    }

    private double calcularMinimo() {
        return inventarios.stream()
                .mapToDouble(inventario -> inventario.getCantidad() * inventario.getPrecio())
                .min()
                .orElse(0.0);
    }

    public void exportar(HttpServletResponse response) throws IOException, DocumentException {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());

        documento.open();

        // Cargar el logo
        try {
            InputStream logoStream = getClass().getClassLoader().getResourceAsStream("static/images/logo.png");
            if (logoStream != null) {
                byte[] logoBytes = logoStream.readAllBytes();
                Image logo = Image.getInstance(logoBytes);
                logo.scaleToFit(100, 100);
                logo.setAlignment(Image.ALIGN_LEFT);
                documento.add(logo);
            } else {
                System.out.println("Error: No se pudo encontrar el archivo logo.png");
            }
        } catch (Exception e) {
            System.out.println("Error al cargar el logo: " + e.getMessage());
        }

// Espaciado después del logo
        documento.add(new Paragraph("\n"));

        // Título de la empresa
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(0, 51, 102));
        Paragraph titulo = new Paragraph("Salud Vida", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);

        // Subtítulo de la sección
        Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(0, 51, 102));
        Paragraph subtitulo = new Paragraph("Reporte de mínimo/promedio/máximo", fuenteSubtitulo);
        subtitulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(subtitulo);

        // Espaciado después del subtítulo
        documento.add(new Paragraph("\n"));

        // Dirección y contacto
        Font fuenteTexto = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
        Paragraph direccion = new Paragraph("Dirección: Calle Ficticia 123", fuenteTexto);
        direccion.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(direccion);

        Paragraph contacto = new Paragraph("Contacto: info@saludvida.com", fuenteTexto);
        contacto.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(contacto);

        // Fecha de generación
        Paragraph fechaGeneracion = new Paragraph("Fecha de generación: " + new Date().toString(), fuenteTexto);
        fechaGeneracion.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(fechaGeneracion);

        // Espaciado después de la fecha
        documento.add(new Paragraph("\n"));

        // Tabla de inventario
        PdfPTable tablaInventario = new PdfPTable(7);
        tablaInventario.setWidthPercentage(100);
        tablaInventario.setSpacingBefore(10);
        tablaInventario.setWidths(new float[]{1, 2, 1, 1, 1, 1, 1});

        // Encabezados de la tabla de inventario
        String[] encabezados = {"ID Inventario", "Producto", "Cantidad", "Precio", "Estado", "Total", "Fecha"};
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell celdaEncabezado;
        for (String encabezado : encabezados) {
            celdaEncabezado = new PdfPCell(new Phrase(encabezado, fuenteEncabezado));
            celdaEncabezado.setBackgroundColor(new Color(0, 51, 102));
            celdaEncabezado.setPadding(5);
            celdaEncabezado.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaInventario.addCell(celdaEncabezado);
        }

        // Filas de la tabla de inventario
        for (Inventario inventario : inventarios) {
            tablaInventario.addCell(String.valueOf(inventario.getIdInventario()));
            tablaInventario.addCell(inventario.getProducto().getNombre());
            tablaInventario.addCell(String.valueOf(inventario.getCantidad()));
            tablaInventario.addCell(String.format("%.2f", inventario.getPrecio()));
            tablaInventario.addCell(inventario.getEstado());
            tablaInventario.addCell(String.format("%.2f", inventario.getCantidad() * inventario.getPrecio()));
            tablaInventario.addCell(inventario.getFecha().toString());
        }

        documento.add(tablaInventario);

        // Espaciado después de la tabla de inventario
        documento.add(new Paragraph("\n"));

        // Encabezado de estadísticas
        Paragraph tituloEstadisticas = new Paragraph("Reporte de Inventario - Promedio, Máximo y Mínimo", fuenteTitulo);
        tituloEstadisticas.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(tituloEstadisticas);

        // Espaciado después del título de estadísticas
        documento.add(new Paragraph("\n"));

        // Tabla de estadísticas
        PdfPTable tablaEstadisticas = new PdfPTable(2);
        tablaEstadisticas.setWidthPercentage(100);
        tablaEstadisticas.setSpacingBefore(10);
        tablaEstadisticas.setWidths(new float[]{3.0f, 2.0f});

        // Encabezados de la tabla de estadísticas
        fuenteEncabezado.setColor(Color.WHITE);
        PdfPCell celdaEstadistica = new PdfPCell(new Phrase("Estadística", fuenteEncabezado));
        celdaEstadistica.setBackgroundColor(new Color(0, 51, 102));
        celdaEstadistica.setPadding(5);
        celdaEstadistica.setHorizontalAlignment(Element.ALIGN_CENTER);
        tablaEstadisticas.addCell(celdaEstadistica);
        celdaEstadistica.setPhrase(new Phrase("Valor", fuenteEncabezado));
        tablaEstadisticas.addCell(celdaEstadistica);

        // Valores de la tabla de estadísticas
        tablaEstadisticas.addCell("Promedio Total");
        tablaEstadisticas.addCell(String.format("%.2f", calcularPromedio()));

        tablaEstadisticas.addCell("Máximo Total");
        tablaEstadisticas.addCell(String.format("%.2f", calcularMaximo()));

        tablaEstadisticas.addCell("Mínimo Total");
        tablaEstadisticas.addCell(String.format("%.2f", calcularMinimo()));

        documento.add(tablaEstadisticas);

        documento.close();
    }
}
