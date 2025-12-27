package org.example.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Excel导出工具类
 */
@Slf4j
public class ExcelExportUtil {

    /**
     * 导出Excel
     * @param sheetName 工作表名称
     * @param headers 表头
     * @param dataList 数据列表
     * @return Excel字节数组
     */
    public static byte[] exportExcel(String sheetName, List<String> headers, List<List<Object>> dataList) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000); // 设置列宽
            }

            // 创建数据样式
            CellStyle dataStyle = createDataStyle(workbook);

            // 填充数据
            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                List<Object> rowData = dataList.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = rowData.get(j);
                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                    cell.setCellStyle(dataStyle);
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        
        // 设置对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置对齐
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }

    /**
     * 导出多Sheet Excel
     * @param sheets 多个Sheet数据，key为sheet名称
     * @return Excel字节数组
     */
    public static byte[] exportMultiSheetExcel(Map<String, SheetData> sheets) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            for (Map.Entry<String, SheetData> entry : sheets.entrySet()) {
                String sheetName = entry.getKey();
                SheetData sheetData = entry.getValue();

                Sheet sheet = workbook.createSheet(sheetName);

                // 创建表头
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < sheetData.getHeaders().size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(sheetData.getHeaders().get(i));
                    cell.setCellStyle(headerStyle);
                    sheet.setColumnWidth(i, 4000);
                }

                // 填充数据
                for (int i = 0; i < sheetData.getDataList().size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    List<Object> rowData = sheetData.getDataList().get(i);
                    for (int j = 0; j < rowData.size(); j++) {
                        Cell cell = row.createCell(j);
                        Object value = rowData.get(j);
                        if (value != null) {
                            if (value instanceof Number) {
                                cell.setCellValue(((Number) value).doubleValue());
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("导出多Sheet Excel失败", e);
            throw new RuntimeException("导出多Sheet Excel失败", e);
        }
    }

    /**
     * Sheet数据封装类
     */
    public static class SheetData {
        private List<String> headers;
        private List<List<Object>> dataList;

        public SheetData(List<String> headers, List<List<Object>> dataList) {
            this.headers = headers;
            this.dataList = dataList;
        }

        public List<String> getHeaders() {
            return headers;
        }

        public List<List<Object>> getDataList() {
            return dataList;
        }
    }
}
