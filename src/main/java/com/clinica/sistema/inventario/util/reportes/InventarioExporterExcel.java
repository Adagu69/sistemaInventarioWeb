package com.clinica.sistema.inventario.util.reportes;

import com.clinica.sistema.inventario.model.Inventario;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InventarioExporterExcel {
    private final List<Inventario> listaInventarios;
    private final Workbook workbook;
    private final Sheet sheet;

    public InventarioExporterExcel(List<Inventario> listaInventarios) {
        this.listaInventarios = listaInventarios;
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet("Inventario");
    }

    private void escribirCabeceraDeTabla() {
        Row row = sheet.createRow(0);
        CellStyle estilo = workbook.createCellStyle();
        Font fuente = workbook.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        estilo.setFont(fuente);
        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] columnas = {"ID", "Producto", "Cantidad", "Precio", "Estado", "Fecha"};

        for (int i = 0; i < columnas.length; i++) {
            Cell celda = row.createCell(i);
            celda.setCellValue(columnas[i]);
            celda.setCellStyle(estilo);
        }
    }

    private void escribirDatosDeTabla() {
        int rowCount = 1;
        for (Inventario inventario : listaInventarios) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(inventario.getIdInventario());
            row.createCell(1).setCellValue(inventario.getProducto().getNombre());
            row.createCell(2).setCellValue(inventario.getCantidad());
            row.createCell(3).setCellValue(inventario.getPrecio());
            row.createCell(4).setCellValue(inventario.getEstado());
            row.createCell(5).setCellValue(inventario.getFecha().toString());
        }
        // Auto ajustar el ancho de las columnas
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public void exportar(HttpServletResponse response) throws IOException {
        escribirCabeceraDeTabla();
        escribirDatosDeTabla();

        // Establecer encabezados para descarga
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String headerValue = "attachment; filename=Inventario_" + fecha + ".xlsx";
        response.setHeader("Content-Disposition", headerValue);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}