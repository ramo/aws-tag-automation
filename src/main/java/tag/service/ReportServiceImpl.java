package tag.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tag.model.ReportData;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportServiceImpl implements ReportService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    @Override
    public void generateReport(String outDir, ReportData rd) throws Exception {
        System.out.println("Report generation started. Please wait...");
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(rd.getReportName());

        XSSFFont defaultFont = (XSSFFont) wb.createFont();
        defaultFont.setFontHeightInPoints((short)10);
        defaultFont.setFontName("Arial");
        defaultFont.setBold(false);
        defaultFont.setItalic(false);

        XSSFFont font= (XSSFFont) wb.createFont();
        font.setFontHeightInPoints((short)10);
        font.setFontName("Arial");
        font.setBold(true);
        font.setItalic(false);

        int r = 0;
        writeData(wb, sheet, r++, rd.getHeader(), font, true);

        for (Object[] row : rd.getData()) {
            writeData(wb, sheet, r++, row, defaultFont, false);
        }

        for (int c = 0; c < rd.getHeader().length; c++) {
            sheet.autoSizeColumn(c);
        }

        String fileName = outDir + File.separator + rd.getReportName() + "-" + sdf.format(new Date()) +  ".xlsx";
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(fileName)))) {
            wb.write(os);
        }

        System.out.println("Report :" + fileName + " generated successfully.");
    }

    private void writeData(Workbook wb, Sheet sheet, int r, Object[] data, Font font, boolean header) {
        Row row = sheet.createRow(r);
        for (int c = 0; c < data.length; c++) {
            Cell cell = row.createCell(c);
            cell.setCellValue(data[c].toString());
            CellStyle style = wb.createCellStyle();
            if (header) {
                style.setAlignment(CellStyle.ALIGN_CENTER);
            }
            style.setFont(font);
            cell.setCellStyle(style);
        }
    }
}
