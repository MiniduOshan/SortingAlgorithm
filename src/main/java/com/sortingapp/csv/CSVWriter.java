package com.sortingapp.csv;

import com.sortingapp.model.CSVData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for exporting sorted datasets or performance reports to CSV files.
 * 
 * Group Assignment Contribution:
 * Member 1: Dataset handling and CSV export.
 */
public class CSVWriter {

    /**
     * Exports sorted column data along with original dataset, re-ordered by the sorted column.
     * 
     * @param targetFile File to write to.
     * @param originalData The original CSVData object.
     * @param colIndex The index of the sorted column.
     * @param sortedValues The array of sorted double values.
     * @throws IOException If file writing fails.
     */
    public static void exportSortedCSV(File targetFile, CSVData originalData, int colIndex, double[] sortedValues) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile))) {
            // Write Header
            List<String> headers = originalData.getHeaders();
            bw.write(String.join(",", headers));
            bw.newLine();

            // Re-order rows based on sortedValues
            // We match sorted double values with original rows
            List<String[]> rows = originalData.getRows();
            boolean[] used = new boolean[rows.size()];

            for (double val : sortedValues) {
                for (int i = 0; i < rows.size(); i++) {
                    if (!used[i]) {
                        String[] row = rows.get(i);
                        double rowVal = 0.0;
                        if (colIndex < row.length) {
                            try {
                                rowVal = Double.parseDouble(row[colIndex].trim());
                            } catch (NumberFormatException ignored) {}
                        }
                        if (Double.compare(val, rowVal) == 0) {
                            used[i] = true;
                            bw.write(formatRowCSV(row));
                            bw.newLine();
                            break;
                        }
                    }
                }
            }
        }
    }

    private static String formatRowCSV(String[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            if (i > 0) sb.append(",");
            String cell = row[i];
            if (cell.contains(",") || cell.contains("\"")) {
                cell = "\"" + cell.replace("\"", "\"\"") + "\"";
            }
            sb.append(cell);
        }
        return sb.toString();
    }
}
