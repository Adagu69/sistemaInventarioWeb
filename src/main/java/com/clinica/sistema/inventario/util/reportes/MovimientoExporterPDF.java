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

@Component // Anotación para que Spring la detecte como un componente
public class MovimientoExporterPDF {

    public void exportar(List<Movimiento> entradas, List<Movimiento> salidas, HttpServletResponse response) throws DocumentException, IOException {
        // Configuración del documento PDF
        Document document = new Document(PageSize.A4);
        try {
            // Configurar la respuesta HTTP
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=movimientos.pdf");

            // Crear el escritor de PDF
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

            // Agregar título
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
            fuenteTitulo.setColor(new Color(0, 51, 102)); // Azul oscuro
            Paragraph titulo = new Paragraph("Clinica Mundo Salud", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            // Agregar subtítulo
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            fuenteSubtitulo.setColor(new Color(0, 51, 102)); // Azul oscuro
            Paragraph subtitulo = new Paragraph("Reporte de Movimientos", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            // Información de contacto y dirección
            Font fuenteInfo = FontFactory.getFont(FontFactory.HELVETICA, 12);
            fuenteInfo.setColor(Color.BLACK);
            Paragraph contacto = new Paragraph("Contacto: clinicacms@gmail.com", fuenteInfo);
            contacto.setAlignment(Element.ALIGN_CENTER);
            document.add(contacto);

            Paragraph direccion = new Paragraph("Dirección: Avenida Carlos Izaguirre cuadra 16", fuenteInfo);
            direccion.setAlignment(Element.ALIGN_CENTER);
            document.add(direccion);

            // Fecha de generación
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = "Fecha de generación: " + sdf.format(new Date());
            Paragraph fechaGeneracion = new Paragraph(fecha, fuenteInfo);
            fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
            document.add(fechaGeneracion);

            // Espacio entre el encabezado y la tabla
            document.add(Chunk.NEWLINE);

            // Agregar la sección de entradas
            agregarSeccion(document, "Entradas", entradas);
            document.add(Chunk.NEWLINE);

            // Agregar la sección de salidas
            agregarSeccion(document, "Salidas", salidas);

            // Agregar resumen de totales
            agregarResumen(document, entradas.size(), salidas.size());

            // Cerrar el documento
            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }

    private void agregarSeccion(Document document, String tituloSeccion, List<Movimiento> movimientos) throws DocumentException {
        // Título de la sección
        Font fuenteTituloSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        fuenteTituloSeccion.setColor(new Color(0, 51, 102)); // Azul oscuro
        Paragraph titulo = new Paragraph(tituloSeccion, fuenteTituloSeccion);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        // Crear tabla para la sección
        PdfPTable table = new PdfPTable(5); // Número de columnas
        table.setWidthPercentage(100);
        table.setSpacingBefore(15);

        // Configuración de los encabezados de la tabla
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuenteEncabezado.setColor(Color.WHITE);
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(new Color(0, 51, 102)); // Azul oscuro
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
            table.addCell(movimiento.getFecha().toString()); // Acceso a la fecha del movimiento
        }

        // Agregar la tabla al documento
        document.add(table);
    }

    private void agregarResumen(Document document, int totalEntradas, int totalSalidas) throws DocumentException {
        // Crear un párrafo con el resumen
        Font fuenteResumen = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        fuenteResumen.setColor(new Color(0, 51, 102)); // Azul oscuro
        Paragraph resumen = new Paragraph(
                String.format("Resumen:\nTotal de Entradas: %d\nTotal de Salidas: %d", totalEntradas, totalSalidas),
                fuenteResumen
        );
        resumen.setAlignment(Element.ALIGN_LEFT);
        document.add(resumen);
    }
}
