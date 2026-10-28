package com.sortingapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the dataset loaded from a CSV file.
 * Stores headers, row data, and performs strict validation to identify valid numeric columns
 * suitable for sorting without silent data corruption or zero-filling.
 * 
 * Group Assignment Contribution:
 * Member 1: File upload functionality and dataset handling.
 */
public class CSVData {
    private final String fileName;
    private final List<String> headers;
    private final List<String[]> rows;
    private final List<Integer> numericColumnIndices;
    private final Map<Integer, String> columnValidationErrors;

    public CSVData(String fileName, List<String> headers, List<String[]> rows) {
        this.fileName = fileName;
        this.headers = Collections.unmodifiableList(new ArrayList<>(headers));
        this.rows = Collections.unmodifiableList(new ArrayList<>(rows));

        Map<Integer, String> errors = new HashMap<>();
        this.numericColumnIndices = analyzeNumericColumns(errors);
        this.columnValidationErrors = Collections.unmodifiableMap(errors);
    }

    /**
     * Analyzes each column to check if it contains valid, finite numeric values across ALL rows.
     * A column is only considered numeric if every single row contains a parseable, finite number
     * (negatives, decimals, and duplicates are supported). Columns with missing values, empty strings,
     * non-numeric text, NaN, or Infinities are strictly marked invalid with descriptive error reasons.
     * 
     * @param errors Map populated with validation error reasons for non-numeric columns.
     * @return List of column indices that are strictly numeric and safe to sort.
     */
    private List<Integer> analyzeNumericColumns(Map<Integer, String> errors) {
        List<Integer> numericIndices = new ArrayList<>();
        int colCount = headers.size();

        if (rows.isEmpty()) {
            for (int col = 0; col < colCount; col++) {
                errors.put(col, "Column contains no data rows.");
            }
            return Collections.emptyList();
        }

        for (int col = 0; col < colCount; col++) {
            String colName = headers.get(col);
            String columnError = null;

            for (int r = 0; r < rows.size(); r++) {
                String[] row = rows.get(r);
                int displayRow = r + 1;

                if (col >= row.length) {
                    columnError = String.format("Row %d is missing column '%s' (inconsistent row length %d vs %d).",
                            displayRow, colName, row.length, colCount);
                    break;
                }

                String val = row[col].trim();
                if (val.isEmpty()) {
                    columnError = String.format("Row %d contains an empty or missing value in column '%s'.",
                            displayRow, colName);
                    break;
                }

                try {
                    double d = Double.parseDouble(val);
                    if (!Double.isFinite(d)) {
                        columnError = String.format("Row %d contains a non-finite value ('%s') in column '%s'.",
                                displayRow, val, colName);
                        break;
                    }
                } catch (NumberFormatException e) {
                    columnError = String.format("Row %d contains non-numeric text ('%s') in column '%s'.",
                            displayRow, val, colName);
                    break;
                }
            }

            if (columnError == null) {
                numericIndices.add(col);
            } else {
                errors.put(col, columnError);
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
     * Returns the validation error reason why a column cannot be sorted,
     * or null if the column is a valid numeric column.
     * 
     * @param colIndex Column index to inspect.
     * @return Descriptive error string, or null if valid.
     */
    public String getColumnValidationError(int colIndex) {
        if (colIndex < 0 || colIndex >= headers.size()) {
            return "Column index " + colIndex + " is out of bounds.";
        }
        return columnValidationErrors.get(colIndex);
    }

    /**
     * Extracts values from a specified column into an array of double.
     * Strictly verifies that the column is a valid numeric column.
     * Never silently replaces invalid, missing, or non-finite numbers with 0.0.
     * 
     * @param colIndex Column index to extract.
     * @return double[] containing the column values.
     * @throws IllegalArgumentException If the column index is invalid or contains non-numeric data.
     */
    public double[] getColumnValuesAsDouble(int colIndex) {
        if (colIndex < 0 || colIndex >= headers.size()) {
            throw new IllegalArgumentException("Column index out of range: " + colIndex);
        }

        if (!isColumnNumeric(colIndex)) {
            String reason = getColumnValidationError(colIndex);
            throw new IllegalArgumentException(String.format(
                    "Column '%s' cannot be extracted as numeric data: %s",
                    headers.get(colIndex),
                    (reason != null ? reason : "Invalid numeric values detected.")
            ));
        }

        double[] values = new double[rows.size()];
        for (int i = 0; i < rows.size(); i++) {
            values[i] = Double.parseDouble(rows.get(i)[colIndex].trim());
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
