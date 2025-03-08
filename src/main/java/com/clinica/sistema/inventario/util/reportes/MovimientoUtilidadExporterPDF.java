package com.clinica.sistema.inventario.util.reportes;

import com.clinica.sistema.inventario.model.Movimiento;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class MovimientoUtilidadExporterPDF {

    public void exportar(List<Movimiento> entradas, List<Movimiento> salidas, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=ingresos_utilidad.pdf");

            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Agregar el logo al documento
            try {
                InputStream logoStream = getClass().getClassLoader().getResourceAsStream("static/images/logo.png");
                if (logoStream == null) {
                    throw new IOException("No se pudo encontrar el archivo logo.png en el directorio estático.");
                }
                byte[] logoBytes = logoStream.readAllBytes();
                Image logo = Image.getInstance(logoBytes);
                logo.scaleToFit(100, 100);
                logo.setAlignment(Image.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception e) {
                System.out.println("Error al cargar el logo: " + e.getMessage());
            }

            // Agregar encabezado y título
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
            fuenteTitulo.setColor(new Color(0, 51, 102)); // Azul oscuro
            Paragraph titulo = new Paragraph("Reporte de Ingresos/Utilidad de Inventario", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            // Agregar subtítulo
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            fuenteSubtitulo.setColor(new Color(0, 51, 102)); // Azul oscuro
            Paragraph subtitulo = new Paragraph("Reporte de Utilidad", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            // Información de contacto y dirección
            Font fuenteInfo = FontFactory.getFont(FontFactory.HELVETICA, 12);
            fuenteInfo.setColor(Color.BLACK);
            Paragraph contacto = new Paragraph("Contacto: info@saludvida.com", fuenteInfo);
            contacto.setAlignment(Element.ALIGN_CENTER);
            document.add(contacto);

            Paragraph direccion = new Paragraph("Dirección: Calle Ficticia 123", fuenteInfo);
            direccion.setAlignment(Element.ALIGN_CENTER);
            document.add(direccion);

            // Fecha de generación
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = "Fecha de generación: " + sdf.format(new Date());
            Paragraph fechaGeneracion = new Paragraph(fecha, FontFactory.getFont(FontFactory.HELVETICA, 12));
            fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
            document.add(fechaGeneracion);

            document.add(Chunk.NEWLINE);

            // Agregar la sección de entradas
            agregarSeccion(document, "Entradas", entradas);

            document.add(Chunk.NEWLINE);

            // Agregar la sección de salidas
            agregarSeccion(document, "Salidas", salidas);

            // Agregar resumen de utilidad
            agregarResumen(document, entradas, salidas);

            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }

    private void agregarSeccion(Document document, String tituloSeccion, List<Movimiento> movimientos) throws DocumentException {
        // Agregar título de la sección
        Font fuenteTituloSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        fuenteTituloSeccion.setColor(new Color(0, 51, 102));
        Paragraph titulo = new Paragraph(tituloSeccion, fuenteTituloSeccion);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        // Crear tabla para la sección
        PdfPTable table = new PdfPTable(5); // Número de columnas
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);

        // Encabezados de la tabla
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuenteEncabezado.setColor(Color.WHITE);
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(new Color(0, 51, 102));
        celda.setPadding(5);

        String[] encabezados = {"ID", "Producto", "Cantidad", "Precio", "Fecha"};
        for (String encabezado : encabezados) {
            celda.setPhrase(new Phrase(encabezado, fuenteEncabezado));
            table.addCell(celda);
        }

        // Agregar datos a la tabla
        Font fuenteDatos = FontFactory.getFont(FontFactory.HELVETICA);
        fuenteDatos.setColor(Color.BLACK);
        for (Movimiento movimiento : movimientos) {
            table.addCell(String.valueOf(movimiento.getIdMovimiento()));
            table.addCell(movimiento.getProducto().getNombre());
            table.addCell(String.valueOf(movimiento.getCantidad()));
            table.addCell(String.valueOf(movimiento.getPrecio()));
            table.addCell(movimiento.getFecha().toString());
        }

        // Agregar la tabla al documento
        document.add(table);
    }

    private void agregarResumen(Document document, List<Movimiento> entradas, List<Movimiento> salidas) throws DocumentException {
        double totalEntradas = entradas.stream().mapToDouble(m -> m.getCantidad() * m.getPrecio()).sum();
        double totalSalidas = salidas.stream().mapToDouble(m -> m.getCantidad() * m.getPrecio()).sum();
        double utilidadBruta = totalEntradas - totalSalidas;

        Font fuenteResumen = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        fuenteResumen.setColor(new Color(0, 51, 102));
        Paragraph resumen = new Paragraph(
                String.format("Resumen:\nTotal De Ingresos En Entradas: %.2f\nTotal De Ingresos En Salidas: %.2f\nUtilidad Bruta: %.2f",
                        totalEntradas, totalSalidas, utilidadBruta),
                fuenteResumen
        );
        resumen.setAlignment(Element.ALIGN_LEFT);
        document.add(resumen);
    }
}
