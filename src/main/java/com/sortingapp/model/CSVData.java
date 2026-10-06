package com.sortingapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the dataset loaded from a CSV file.
 * Stores headers, row data, and identifies numeric columns suitable for sorting.
 * 
 * Group Assignment Contribution:
 * Member 1: File upload functionality and dataset handling.
 */
public class CSVData {
    private final String fileName;
    private final List<String> headers;
    private final List<String[]> rows;
    private final List<Integer> numericColumnIndices;

    public CSVData(String fileName, List<String> headers, List<String[]> rows) {
        this.fileName = fileName;
        this.headers = Collections.unmodifiableList(new ArrayList<>(headers));
        this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
        this.numericColumnIndices = analyzeNumericColumns();
    }

    /**
     * Analyzes each column to check if it contains numeric values.
     * A column is considered numeric if most of its non-empty entries can be parsed as Double.
     * 
     * @return List of column indices that are numeric.
     */
    private List<Integer> analyzeNumericColumns() {
        List<Integer> numericIndices = new ArrayList<>();
        int colCount = headers.size();

        for (int col = 0; col < colCount; col++) {
            int numericCount = 0;
            int totalChecked = 0;

            for (String[] row : rows) {
                if (col < row.length) {
                    String val = row[col].trim();
                    if (!val.isEmpty()) {
                        totalChecked++;
                        try {
                            Double.parseDouble(val);
                            numericCount++;
                        } catch (NumberFormatException ignored) {
                            // Non-numeric value found
                        }
                    }
                }
            }

            // If at least 75% of non-empty values are numeric, classify column as numeric
            if (totalChecked > 0 && ((double) numericCount / totalChecked) >= 0.75) {
                numericIndices.add(col);
            }
        }
        return Collections.unmodifiableList(numericIndices);
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<String[]> getRows() {
        return rows;
    }

    public List<Integer> getNumericColumnIndices() {
        return numericColumnIndices;
    }

    public List<String> getNumericColumnNames() {
        List<String> names = new ArrayList<>();
        for (int idx : numericColumnIndices) {
            names.add(headers.get(idx));
        }
        return names;
    }

    public boolean isColumnNumeric(int colIndex) {
        return numericColumnIndices.contains(colIndex);
    }

    /**
     * Extracts values from a specified column into an array of double.
     * Rows with unparseable numbers are replaced with 0.0 or skipped.
     * 
     * @param colIndex Column index to extract.
     * @return double[] containing the column values.
     */
    public double[] getColumnValuesAsDouble(int colIndex) {
        double[] values = new double[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (colIndex < row.length) {
                String val = row[colIndex].trim();
                try {
                    values[i] = Double.parseDouble(val);
                } catch (NumberFormatException e) {
                    values[i] = 0.0;
                }
            } else {
                values[i] = 0.0;
            }
        }
        return values;
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return headers.size();
    }
}
