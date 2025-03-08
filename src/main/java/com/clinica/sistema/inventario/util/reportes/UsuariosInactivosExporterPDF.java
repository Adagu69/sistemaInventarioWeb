package com.clinica.sistema.inventario.util.reportes;

import com.clinica.sistema.inventario.model.Estado;
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

public class UsuariosInactivosExporterPDF {

    public void exportar(List<Usuario> usuarios, HttpServletResponse response) throws IOException {
        // Configurar la respuesta HTTP para exportar como archivo PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios_inactivos_report.pdf");

        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Añadir el logo al documento
            try {
                InputStream logoStream = getClass().getClassLoader().getResourceAsStream("static/images/logo.png");
                if (logoStream != null) {
                    byte[] logoBytes = logoStream.readAllBytes();
                    Image logo = Image.getInstance(logoBytes);
                    logo.scaleToFit(100, 100);
                    logo.setAlignment(Image.ALIGN_LEFT);
                    document.add(logo);
                } else {
                    System.out.println("Error: No se pudo encontrar el archivo logo.png");
                }
            } catch (Exception e) {
                System.out.println("Error al cargar el logo: " + e.getMessage());
            }

            // Título del documento
            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fuenteTitulo.setColor(new Color(0, 51, 102)); // Azul oscuro
            fuenteTitulo.setSize(24);
            Paragraph titulo = new Paragraph("Salud Vida", fuenteTitulo);
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(titulo);

            // Subtítulo del documento
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            fuenteSubtitulo.setColor(new Color(0, 51, 102)); // Azul oscuro
            Paragraph subtitulo = new Paragraph("Reporte de Usuario inactivos", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            // Agregar un salto de línea para separar el subtítulo de la siguiente sección
            document.add(Chunk.NEWLINE);

            // Información de contacto y dirección
            Font fuenteInfo = FontFactory.getFont(FontFactory.HELVETICA);
            fuenteInfo.setColor(Color.BLACK);
            fuenteInfo.setSize(12);

            Paragraph contacto = new Paragraph("Contacto: info@saludvida.com", fuenteInfo);
            contacto.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(contacto);

            Paragraph direccion = new Paragraph("Dirección: Calle Ficticia 123", fuenteInfo);
            direccion.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(direccion);

            // Fecha de generación del reporte
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = "Fecha de generación: " + sdf.format(new Date());
            Paragraph fechaGeneracion = new Paragraph(fecha, fuenteInfo);
            fechaGeneracion.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(fechaGeneracion);

            // Espacio entre el encabezado y la tabla
            document.add(Chunk.NEWLINE);

            // Crear la tabla de usuarios inactivos
            PdfPTable tableInactivos = new PdfPTable(6);
            tableInactivos.setWidthPercentage(100);
            tableInactivos.setWidths(new int[]{2, 2, 3, 3, 2, 3});

            // Estilo de los encabezados
            PdfPCell celda = new PdfPCell();
            celda.setBackgroundColor(new Color(0, 51, 102)); // Fondo azul oscuro
            celda.setPadding(5);

            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fuenteEncabezado.setColor(Color.WHITE);

            String[] encabezados = {"ID", "Nombre", "Apellido", "Email", "Estado", "Fecha de Creación"};
            for (String encabezado : encabezados) {
                celda.setPhrase(new Phrase(encabezado, fuenteEncabezado));
                tableInactivos.addCell(celda);
            }

            // Agregar los datos de los usuarios inactivos
            Font fuenteDatos = FontFactory.getFont(FontFactory.HELVETICA);
            fuenteDatos.setColor(Color.BLACK);
            int contadorInactivos = 0;
            for (Usuario usuario : usuarios) {
                if (usuario.getEstado() == Estado.INACTIVO) {
                    tableInactivos.addCell(new Phrase(String.valueOf(usuario.getIdUsuario()), fuenteDatos));
                    tableInactivos.addCell(new Phrase(usuario.getNombre() != null ? usuario.getNombre() : "N/A", fuenteDatos));
                    tableInactivos.addCell(new Phrase(usuario.getApellido(), fuenteDatos));
                    tableInactivos.addCell(new Phrase(usuario.getEmail(), fuenteDatos));
                    tableInactivos.addCell(new Phrase("Inactivo", fuenteDatos));
                    if (usuario.getUsuarioFecha() != null && usuario.getUsuarioFecha().getFecha() != null) {
                        tableInactivos.addCell(new Phrase(usuario.getUsuarioFecha().getFecha().toString(), fuenteDatos));
                    } else {
                        tableInactivos.addCell(new Phrase("N/A", fuenteDatos));
                    }
                    contadorInactivos++;
                }
            }

            document.add(tableInactivos);

            // Agregar el total de usuarios inactivos con negrita y espacio antes
            Font fuenteTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Paragraph totalInactivos = new Paragraph("Total de usuarios inactivos: " + contadorInactivos, fuenteTotal);
            totalInactivos.setAlignment(Paragraph.ALIGN_RIGHT);
            totalInactivos.setSpacingBefore(10);
            document.add(totalInactivos);

            // Salto de página para la tabla de usuarios activos
            document.newPage();

            Font fuenteSubtituloActivos = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            fuenteSubtituloActivos.setColor(new Color(0, 51, 102)); // Azul oscuro
            Paragraph subtituloActivos = new Paragraph("Usuarios Activos", fuenteSubtituloActivos);
            subtituloActivos.setAlignment(Element.ALIGN_CENTER);
            document.add(subtituloActivos);

            // Agregar un salto de línea para separar el subtítulo de la tabla
            document.add(Chunk.NEWLINE);

            // Crear la tabla de usuarios activos
            PdfPTable tableActivos = new PdfPTable(6);
            tableActivos.setWidthPercentage(100);
            tableActivos.setWidths(new int[]{2, 2, 3, 3, 2, 3});

            // Agregar encabezados a la tabla de usuarios activos
            for (String encabezado : encabezados) {
                celda.setPhrase(new Phrase(encabezado, fuenteEncabezado));
                tableActivos.addCell(celda);
            }

            // Agregar los datos de los usuarios activos
            int contadorActivos = 0;
            for (Usuario usuario : usuarios) {
                if (usuario.getEstado() == Estado.ACTIVO) {
                    tableActivos.addCell(new Phrase(String.valueOf(usuario.getIdUsuario()), fuenteDatos));
                    tableActivos.addCell(new Phrase(usuario.getNombre() != null ? usuario.getNombre() : "N/A", fuenteDatos));
                    tableActivos.addCell(new Phrase(usuario.getApellido(), fuenteDatos));
                    tableActivos.addCell(new Phrase(usuario.getEmail(), fuenteDatos));
                    tableActivos.addCell(new Phrase("Activo", fuenteDatos));
                    if (usuario.getUsuarioFecha() != null && usuario.getUsuarioFecha().getFecha() != null) {
                        tableActivos.addCell(new Phrase(usuario.getUsuarioFecha().getFecha().toString(), fuenteDatos));
                    } else {
                        tableActivos.addCell(new Phrase("N/A", fuenteDatos));
                    }
                    contadorActivos++;
                }
            }

            document.add(tableActivos);

            // Agregar el total de usuarios activos con negrita y espacio antes
            Paragraph totalActivos = new Paragraph("Total de usuarios activos: " + contadorActivos, fuenteTotal);
            totalActivos.setAlignment(Paragraph.ALIGN_RIGHT);
            totalActivos.setSpacingBefore(10);
            document.add(totalActivos);

        } catch (Exception e) {
            throw new IOException("Error al generar el archivo PDF", e);
        } finally {
            document.close();
        }
    }
}
