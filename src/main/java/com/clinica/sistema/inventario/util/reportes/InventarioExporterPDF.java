package com.clinica.sistema.inventario.util.reportes;

import com.clinica.sistema.inventario.model.Inventario;
import com.lowagie.text.Font;
import com.lowagie.text.*;
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

public class InventarioExporterPDF {

    private List<Inventario> listaInventarios;

    public InventarioExporterPDF(List<Inventario> listaInventarios) {
        this.listaInventarios = listaInventarios;
    }

    private void escribirCabeceraDeLaTabla(PdfPTable tabla) {
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(new Color(0, 51, 102)); // Fondo azul oscuro
        celda.setPadding(5);

        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuente.setColor(Color.WHITE);

        // Encabezados de columna
        celda.setPhrase(new Phrase("ID", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Nombre", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Cantidad", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Precio", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Estado", fuente));
        tabla.addCell(celda);

        celda.setPhrase(new Phrase("Fecha", fuente));
        tabla.addCell(celda);
    }

    private void escribirDatosDeLaTabla(PdfPTable tabla) {
        for (Inventario inventario : listaInventarios) {
            tabla.addCell(String.valueOf(inventario.getIdInventario()));
            tabla.addCell(inventario.getProducto().getNombre());
            tabla.addCell(String.valueOf(inventario.getCantidad()));
            tabla.addCell(String.valueOf(inventario.getPrecio()));
            tabla.addCell(inventario.getEstado());
            tabla.addCell(inventario.getFecha().toString());
        }
    }

    public void exportar(HttpServletResponse response) throws DocumentException, IOException {
        // Crear el documento PDF con un tamaño A4
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());

        // Abrir el documento
        documento.open();

        // Añadir el logo desde la carpeta resources/static/images/logo.png
        try {
            // Obtener el InputStream del logo desde el recurso estático
            InputStream logoStream = getClass().getClassLoader().getResourceAsStream("static/images/logo.png");
            if (logoStream == null) {
                throw new IOException("No se pudo encontrar el archivo logo.png en el directorio estático.");
            }

            // Convertir InputStream a byte[] para usarlo con iText
            byte[] logoBytes = logoStream.readAllBytes();
            Image logo = Image.getInstance(logoBytes);
            logo.scaleToFit(100, 100);  // Ajusta el tamaño del logo
            logo.setAlignment(Image.ALIGN_LEFT);
            documento.add(logo);
        } catch (Exception e) {
            System.out.println("Error al cargar el logo: " + e.getMessage());
        }

        // Configuración de la fuente para el título
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuenteTitulo.setColor(new Color(0, 51, 102));  // Azul oscuro
        fuenteTitulo.setSize(24);

        // Título del documento
        Paragraph titulo = new Paragraph("Reporte de Inventario", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);

        // Información de contacto y dirección
        Font fuenteInfo = FontFactory.getFont(FontFactory.HELVETICA);
        fuenteInfo.setColor(Color.BLACK);
        fuenteInfo.setSize(12);
        Paragraph contacto = new Paragraph("Contacto: info@clinica.com", fuenteInfo);
        contacto.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(contacto);

        Paragraph direccion = new Paragraph("Dirección: Calle Ejemplo 456", fuenteInfo);
        direccion.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(direccion);

        // Fecha de generación del reporte
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = "Fecha de generación: " + sdf.format(new Date());
        Paragraph fechaGeneracion = new Paragraph(fecha, fuenteInfo);
        fechaGeneracion.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(fechaGeneracion);

        // Espacio entre el encabezado y la tabla
        documento.add(Chunk.NEWLINE);

        // Crear la tabla con 6 columnas (ID, Nombre, Cantidad, Precio, Estado, Fecha)
        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(15);
        tabla.setWidths(new float[] {1f, 2.3f, 2.3f, 3f, 3f, 3f});  // Ancho relativo de las columnas

        // Escribir cabecera y los datos de la tabla
        escribirCabeceraDeLaTabla(tabla);
        escribirDatosDeLaTabla(tabla);

        // Añadir la tabla al documento PDF
        documento.add(tabla);

        // Cerrar el documento
        documento.close();
    }
}