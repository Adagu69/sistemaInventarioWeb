package com.clinica.sistema.inventario.util.reportes;


import com.clinica.sistema.inventario.model.Movimiento;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class MovimientoResumenExporterExcel {


    public void exportar(List<Movimiento> entradas, List<Movimiento> salidas, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Movimientos");

        int fila = 0;
        CellStyle tituloStyle = crearEstilo(workbook, true, IndexedColors.DARK_BLUE.getIndex(), (short) 14);
        CellStyle cabeceraStyle = crearEstilo(workbook, true, IndexedColors.DARK_BLUE.getIndex(), (short) 12);
        CellStyle normalStyle = crearEstilo(workbook, false, IndexedColors.WHITE.getIndex(), (short) 11);

        // Encabezado general
        fila = escribirEncabezado(sheet, fila, tituloStyle, normalStyle);

        // Entradas
        fila = escribirSeccion(sheet, fila, "Entradas", entradas, cabeceraStyle, normalStyle);

        // Salidas
        fila = escribirSeccion(sheet, fila, "Salidas", salidas, cabeceraStyle, normalStyle);

        // Resumen
        Row resumen = sheet.createRow(fila++);
        resumen.createCell(0).setCellValue("Resumen:");
        resumen.getCell(0).setCellStyle(cabeceraStyle);
        sheet.createRow(fila++).createCell(0).setCellValue("Total de Entradas: " + entradas.size());
        sheet.createRow(fila++).createCell(0).setCellValue("Total de Salidas: " + salidas.size());

        // Auto ajuste
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }

        // Configurar respuesta
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Resumen_Movimientos.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private int escribirEncabezado(Sheet sheet, int fila, CellStyle titulo, CellStyle normal) {
        Row r1 = sheet.createRow(fila++);
        r1.createCell(0).setCellValue("Clinica Mundo Salud");
        r1.getCell(0).setCellStyle(titulo);
        Row r2 = sheet.createRow(fila++);
        r2.createCell(0).setCellValue("Reporte de Movimientos");
        r2.getCell(0).setCellStyle(titulo);
        sheet.createRow(fila++).createCell(0).setCellValue("Contacto: clinicacms@gmail.com");
        sheet.createRow(fila++).createCell(0).setCellValue("Dirección: Avenida Carlos Izaguirre cuadra 14");
        sheet.createRow(fila++).createCell(0).setCellValue("Fecha de generación: " + LocalDate.now());
        return fila + 1;
    }

    private int escribirSeccion(Sheet sheet, int fila, String titulo, List<Movimiento> movimientos,
                                CellStyle cabecera, CellStyle normal) {
        sheet.createRow(fila++).createCell(0).setCellValue(titulo);
        Row cabeceraRow = sheet.createRow(fila++);
        String[] headers = {"ID", "Producto", "Cantidad", "Precio", "Fecha"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = cabeceraRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(cabecera);
        }
        for (Movimiento m : movimientos) {
            Row filaDatos = sheet.createRow(fila++);
            filaDatos.createCell(0).setCellValue(m.getIdMovimiento());
            filaDatos.createCell(1).setCellValue(m.getProducto().getNombre());
            filaDatos.createCell(2).setCellValue(m.getCantidad());
            filaDatos.createCell(3).setCellValue(m.getPrecio());
            filaDatos.createCell(4).setCellValue(m.getFecha().toString());
        }
        return fila + 1;
    }

    private CellStyle crearEstilo(Workbook wb, boolean bold, short bg, short size) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(bold);
        font.setFontHeightInPoints(size);
        if (bg != IndexedColors.WHITE.getIndex()) {
            style.setFillForegroundColor(bg);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            font.setColor(IndexedColors.WHITE.getIndex());
        }
        style.setFont(font);
        return style;
    }
}