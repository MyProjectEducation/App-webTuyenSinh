package util;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelSmartUtils {
    public static String normalizeHeader(String headerName) {
        if (headerName == null) return "";
        String s = headerName.toLowerCase();
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.replace('đ', 'd');
        s = s.replaceAll("[^a-z0-9]", "");
        return s;
    }

    public static List<Map<String, String>> smartScan(File file, Map<String, String> dictionary) throws IOException {
        List<Map<String, String>> allRows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file)) {
            DataFormatter formatter = new DataFormatter();
            for (Sheet sheet : workbook) {
                List<List<String>> rows = readSheetRows(sheet, formatter);
                int headerRowIndex = -1;
                List<String> recognizedHeaders = new ArrayList<>();

                int maxScan = Math.min(rows.size(), 20);
                for (int i = 0; i < maxScan; i++) {
                    List<String> row = rows.get(i);
                    int matchCount = 0;
                    List<String> tempHeaders = new ArrayList<>();
                    for (String cell : row) {
                        String norm = normalizeHeader(cell);
                        tempHeaders.add(norm);
                        if (dictionary.containsKey(norm)) {
                            matchCount++;
                        }
                    }
                    if (matchCount >= 2) {
                        headerRowIndex = i;
                        recognizedHeaders = tempHeaders;
                        break;
                    }
                }

                if (headerRowIndex != -1) {
                    for (int i = headerRowIndex + 1; i < rows.size(); i++) {
                        List<String> row = rows.get(i);
                        if (isRowEmpty(row)) continue;

                        Map<String, String> cleanRow = new HashMap<>();
                        for (int j = 0; j < recognizedHeaders.size(); j++) {
                            String key = recognizedHeaders.get(j);
                            String dbCol = dictionary.get(key);
                            if (dbCol == null) continue;
                            String value = j < row.size() ? row.get(j) : "";
                            if (value != null && !value.trim().isEmpty()) {
                                cleanRow.put(dbCol, value.trim());
                            }
                        }
                        if (!cleanRow.isEmpty()) {
                            allRows.add(cleanRow);
                        }
                    }
                }
            }
        }
        return allRows;
    }

    private static List<List<String>> readSheetRows(Sheet sheet, DataFormatter formatter) {
        List<List<String>> rows = new ArrayList<>();
        int lastRow = sheet.getLastRowNum();
        for (int i = 0; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                rows.add(new ArrayList<>());
                continue;
            }
            int lastCell = row.getLastCellNum();
            List<String> cells = new ArrayList<>();
            for (int c = 0; c < Math.max(lastCell, 0); c++) {
                String val = formatter.formatCellValue(row.getCell(c));
                cells.add(val == null ? "" : val.trim());
            }
            rows.add(cells);
        }
        return rows;
    }

    private static boolean isRowEmpty(List<String> row) {
        for (String cell : row) {
            if (cell != null && !cell.trim().isEmpty()) return false;
        }
        return true;
    }
}
