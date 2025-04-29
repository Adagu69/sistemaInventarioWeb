package com.clinica.sistema.inventario.util.reportes;

import com.clinica.sistema.inventario.model.Usuario;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioFechaExporterPDF {

    public void generarReportePorFecha(List<Usuario> usuarios, HttpServletResponse response) throws DocumentException, IOException {
        // Configuración del documento PDF
        Document document = new Document();
        try {
            // Configurar la respuesta HTTP
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=usuarios_por_fecha.pdf");

            // Crear el escritor de PDF
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Añadir el logo desde la carpeta resources/static/images/logo.png
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

            // Título del documento
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
            fuenteTitulo.setColor(new Color(0, 51, 102)); // Azul oscuro
            Paragraph titulo = new Paragraph("Salud Vida", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            // Subtítulo
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            fuenteSubtitulo.setColor(new Color(0, 51, 102)); // Mismo color para mantener la coherencia
            Paragraph subtitulo = new Paragraph("Reporte por fecha", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            document.add(Chunk.NEWLINE);

            // Información de contacto y dirección
            Font fuenteInfo = FontFactory.getFont(FontFactory.HELVETICA, 12);
            fuenteInfo.setColor(Color.BLACK);
            Paragraph contacto = new Paragraph("Contacto: info@saludvida.com", fuenteInfo);
            contacto.setAlignment(Element.ALIGN_CENTER);
            document.add(contacto);

            Paragraph direccion = new Paragraph("Dirección: Calle Ficticia 123", fuenteInfo);
            direccion.setAlignment(Element.ALIGN_CENTER);
            document.add(direccion);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = "Fecha de generación: " + sdf.format(new Date());
            Paragraph fechaGeneracion = new Paragraph(fecha, fuenteInfo);
            fechaGeneracion.setAlignment(Element.ALIGN_CENTER);
            document.add(fechaGeneracion);

            document.add(Chunk.NEWLINE);

            // Agrupar usuarios por mes y año
            Map<Integer, List<Usuario>> usuariosPorMes = usuarios.stream()
                    .collect(Collectors.groupingBy(usuario -> usuario.getUsuarioFecha().getFecha().getMonthValue()));

            // Nombres de los meses
            String[] nombresMeses = {
                    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
            };

            // Iterar y crear contenido para cada mes
            for (int i = 0; i < 12; i++) {
                if (usuariosPorMes.containsKey(i + 1)) {
                    // Título del mes
                    Paragraph tituloMes = new Paragraph(nombresMeses[i], FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
                    tituloMes.setAlignment(Element.ALIGN_CENTER);
                    document.add(tituloMes);
                    document.add(Chunk.NEWLINE);

                    // Crear la tabla de usuarios del mes
                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(15);
                    table.setWidths(new float[]{2.5f, 2.5f, 3f, 3f, 2f}); // Anchos de las columnas

                    // Encabezados de la tabla
                    PdfPCell celda;
                    Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                    fuenteEncabezado.setColor(Color.WHITE);
                    celda = new PdfPCell(new Phrase("Nombre", fuenteEncabezado));
                    celda.setBackgroundColor(new Color(0, 51, 102)); // Azul oscuro
                    table.addCell(celda);

                    celda = new PdfPCell(new Phrase("Apellido", fuenteEncabezado));
                    celda.setBackgroundColor(new Color(0, 51, 102));
                    table.addCell(celda);

                    celda = new PdfPCell(new Phrase("Email", fuenteEncabezado));
                    celda.setBackgroundColor(new Color(0, 51, 102));
                    table.addCell(celda);

                    celda = new PdfPCell(new Phrase("Fecha de Creación", fuenteEncabezado));
                    celda.setBackgroundColor(new Color(0, 51, 102));
                    table.addCell(celda);

                    celda = new PdfPCell(new Phrase("Estado", fuenteEncabezado));
                    celda.setBackgroundColor(new Color(0, 51, 102));
                    table.addCell(celda);

                    // Añadir los usuarios a la tabla
                    Font fuenteDatos = FontFactory.getFont(FontFactory.HELVETICA);
                    fuenteDatos.setColor(Color.BLACK);
                    for (Usuario usuario : usuariosPorMes.get(i + 1)) {
                        table.addCell(new Phrase(usuario.getNombre(), fuenteDatos));
                        table.addCell(new Phrase(usuario.getApellido(), fuenteDatos));
                        table.addCell(new Phrase(usuario.getEmail(), fuenteDatos));
                        table.addCell(new Phrase(usuario.getUsuarioFecha().getFecha().toString(), fuenteDatos));
                        table.addCell(new Phrase(usuario.getEstado().toString(), fuenteDatos));
                    }

                    // Añadir la tabla al documento
                    document.add(table);
                    document.add(Chunk.NEWLINE);
                }
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException("Error al generar el reporte: " + e.getMessage(), e);
        }
    }
}
