package com.sortingapp.csv;

import com.sortingapp.model.CSVData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for exporting sorted datasets or performance reports to CSV files.
 * Ensures RFC 4180 compliant CSV formatting, UTF-8 character encoding, proper escaping of
 * headers and data fields, and zero loss of rows.
 * 
 * Group Assignment Contribution:
 * Member 1: Dataset handling and CSV export.
 */
public class CSVWriter {

    /**
     * Exports sorted column data along with the original dataset, re-ordered by the sorted column.
     * Escapes both headers and values properly and guarantees all rows are preserved.
     * 
     * @param targetFile File to write to.
     * @param originalData The original CSVData object.
     * @param colIndex The index of the sorted column.
     * @param sortedValues The array of sorted double values.
     * @throws IOException If file writing fails or invalid parameters are provided.
     */
    public static void exportSortedCSV(File targetFile, CSVData originalData, int colIndex, double[] sortedValues) throws IOException {
        if (targetFile == null) {
            throw new IOException("Target export file cannot be null.");
        }
        if (originalData == null) {
            throw new IOException("Original dataset cannot be null.");
        }
        if (sortedValues == null) {
            throw new IOException("Sorted values cannot be null.");
        }

        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8))) {

            // Write properly escaped Header
            List<String> headers = originalData.getHeaders();
            bw.write(formatRowCSV(headers.toArray(new String[0])));
            bw.newLine();

            List<String[]> rows = originalData.getRows();
            Map<Double, Deque<String[]>> valueToRows = new HashMap<>();
            List<String[]> unparseableRows = new ArrayList<>();

            for (String[] row : rows) {
                if (colIndex >= 0 && colIndex < row.length) {
                    try {
                        double v = Double.parseDouble(row[colIndex].trim());
                        valueToRows.computeIfAbsent(v, k -> new ArrayDeque<>()).add(row);
                    } catch (NumberFormatException e) {
                        unparseableRows.add(row);
                    }
                } else {
                    unparseableRows.add(row);
                }
            }

            // Write rows in the exact order of sorted values
            for (double val : sortedValues) {
                Deque<String[]> matching = valueToRows.get(val);
                if (matching != null && !matching.isEmpty()) {
                    bw.write(formatRowCSV(matching.pollFirst()));
                    bw.newLine();
                }
            }

            // Flush any remaining rows so no data is ever lost
            for (Deque<String[]> remaining : valueToRows.values()) {
                while (!remaining.isEmpty()) {
                    bw.write(formatRowCSV(remaining.pollFirst()));
                    bw.newLine();
                }
            }

            for (String[] unp : unparseableRows) {
                bw.write(formatRowCSV(unp));
                bw.newLine();
            }
        }
    }

    /**
     * Formats a row of cells into a valid CSV line with quoting and quote escaping.
     * 
     * @param row Array of string cell values.
     * @return Formatted CSV line string.
     */
    public static String formatRowCSV(String[] row) {
        if (row == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            String cell = (row[i] == null) ? "" : row[i];
            if (cell.contains(",") || cell.contains("\"") || cell.contains("\n") || cell.contains("\r")) {
                cell = "\"" + cell.replace("\"", "\"\"") + "\"";
            }
            sb.append(cell);
        }
        return sb.toString();
    }
}
