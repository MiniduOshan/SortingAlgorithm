package com.sortingapp;

import com.sortingapp.csv.CSVReader;
import com.sortingapp.csv.CSVWriter;
import com.sortingapp.model.CSVData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive automated tests verifying CSV data validation, handling of missing data,
 * non-finite values, malformed files, and export integrity.
 */
class CSVValidationTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Test CSV Parsing with Quoted Fields, Commas, and Escaped Quotes")
    void testCSVQuotedFields() throws IOException {
        String csvContent = "ID,Description,Score\n" +
                "1,\"Item with, comma\",95.5\n" +
                "2,\"Item with \"\"escaped\"\" quotes\",88.0\n" +
                "3,Normal Item,-12.3\n";

        Path file = tempDir.resolve("quoted_test.csv");
        Files.writeString(file, csvContent);

        CSVData data = CSVReader.readCSV(file.toFile());
        assertEquals(3, data.getHeaders().size());
        assertEquals(3, data.getRowCount());
        assertEquals("Item with, comma", data.getRows().get(0)[1]);
        assertEquals("Item with \"escaped\" quotes", data.getRows().get(1)[1]);
        assertEquals("Normal Item", data.getRows().get(2)[1]);

        // Score should be detected as valid numeric
        assertTrue(data.isColumnNumeric(2));
        double[] scores = data.getColumnValuesAsDouble(2);
        assertArrayEquals(new double[]{95.5, 88.0, -12.3}, scores, 1e-6);
    }

    @Test
    @DisplayName("Test Numeric Column Auto-Detection on Valid Columns")
    void testNumericColumnAutoDetection() {
        List<String> headers = List.of("Name", "Age", "GPA", "Active");
        List<String[]> rows = List.of(
                new String[]{"Alice", "20", "3.85", "true"},
                new String[]{"Bob", "22", "3.20", "false"},
                new String[]{"Charlie", "21", "3.90", "true"}
        );

        CSVData data = new CSVData("test.csv", headers, rows);
        assertFalse(data.isColumnNumeric(0), "Name should not be numeric");
        assertTrue(data.isColumnNumeric(1), "Age should be numeric");
        assertTrue(data.isColumnNumeric(2), "GPA should be numeric");
        assertFalse(data.isColumnNumeric(3), "Active boolean should not be numeric");

        assertEquals(List.of(1, 2), data.getNumericColumnIndices());
        assertEquals(List.of("Age", "GPA"), data.getNumericColumnNames());
    }

    @Test
    @DisplayName("Test Rejection of Columns with Missing Values (Never Zero-Filled)")
    void testRejectMissingValuesNeverZeroFilled() {
        List<String> headers = List.of("ID", "Measurement");
        List<String[]> rows = List.of(
                new String[]{"1", "45.2"},
                new String[]{"2", ""}, // Missing value
                new String[]{"3", "50.1"}
        );

        CSVData data = new CSVData("missing_data.csv", headers, rows);
        assertFalse(data.isColumnNumeric(1), "Column with empty cell must not be classified as numeric");

        String error = data.getColumnValidationError(1);
        assertNotNull(error);
        assertTrue(error.contains("empty or missing"), "Error reason must explain missing value: " + error);

        // Accessing values of invalid column must throw IllegalArgumentException and NOT silently return 0.0
        assertThrows(IllegalArgumentException.class, () -> data.getColumnValuesAsDouble(1));
    }

    @Test
    @DisplayName("Test Rejection of Columns with Non-Numeric Text")
    void testRejectNonNumericText() {
        List<String> headers = List.of("Score");
        List<String[]> rows = List.of(
                new String[]{"98.5"},
                new String[]{"N/A"},
                new String[]{"87.0"}
        );

        CSVData data = new CSVData("text_data.csv", headers, rows);
        assertFalse(data.isColumnNumeric(0));
        String error = data.getColumnValidationError(0);
        assertNotNull(error);
        assertTrue(error.contains("non-numeric text"), "Error reason must specify non-numeric text: " + error);
        assertThrows(IllegalArgumentException.class, () -> data.getColumnValuesAsDouble(0));
    }

    @Test
    @DisplayName("Test Rejection of Columns with Non-Finite Values (NaN / Infinity)")
    void testRejectNonFiniteValues() {
        List<String> headers = List.of("ValNaN", "ValInf");
        List<String[]> rows = List.of(
                new String[]{"10.5", "100.0"},
                new String[]{"NaN", "200.0"},
                new String[]{"20.5", "Infinity"}
        );

        CSVData data = new CSVData("non_finite.csv", headers, rows);
        assertFalse(data.isColumnNumeric(0), "Column with NaN must not be numeric");
        assertFalse(data.isColumnNumeric(1), "Column with Infinity must not be numeric");

        assertTrue(data.getColumnValidationError(0).contains("non-finite"));
        assertTrue(data.getColumnValidationError(1).contains("non-finite"));
    }

    @Test
    @DisplayName("Test Rejection of Empty Columns")
    void testRejectEmptyColumn() {
        List<String> headers = List.of("EmptyCol");
        List<String[]> rows = List.of(
                new String[]{""},
                new String[]{""}
        );

        CSVData data = new CSVData("empty_col.csv", headers, rows);
        assertFalse(data.isColumnNumeric(0));
        assertNotNull(data.getColumnValidationError(0));
    }

    @Test
    @DisplayName("Test Handling of Malformed CSV Files with Unclosed Quotes")
    void testMalformedCSVUnclosedQuotes() {
        String malformed = "ID,Name\n1,\"Unclosed quote name\n2,Bob\n";
        Path file = tempDir.resolve("malformed.csv");
        assertDoesNotThrow(() -> Files.writeString(file, malformed));

        IOException ex = assertThrows(IOException.class, () -> CSVReader.readCSV(file.toFile()));
        assertTrue(ex.getMessage().contains("unclosed quotation mark"),
                "Exception should describe unclosed quotation mark: " + ex.getMessage());
    }

    @Test
    @DisplayName("Test Handling of Empty File and Missing Header")
    void testEmptyAndMissingHeaderFiles() throws IOException {
        Path emptyFile = tempDir.resolve("empty.csv");
        Files.writeString(emptyFile, "");

        assertThrows(IOException.class, () -> CSVReader.readCSV(emptyFile.toFile()));

        Path whitespaceFile = tempDir.resolve("whitespace.csv");
        Files.writeString(whitespaceFile, "   \n\n  \n");

        assertThrows(IOException.class, () -> CSVReader.readCSV(whitespaceFile.toFile()));
    }

    @Test
    @DisplayName("Test CSV Export Correctness, Header Escaping, and Row Preservation")
    void testCSVExport() throws IOException {
        List<String> headers = List.of("ID", "Student, Full Name", "Grade");
        List<String[]> rows = List.of(
                new String[]{"1", "Doe, John", "85.5"},
                new String[]{"2", "Smith, Jane", "92.0"},
                new String[]{"3", "Brown, Charlie", "78.0"},
                new String[]{"4", "Taylor, Alex", "85.5"} // Duplicate grade
        );

        CSVData data = new CSVData("students.csv", headers, rows);
        double[] sortedGrades = new double[]{78.0, 85.5, 85.5, 92.0};

        File exportTarget = tempDir.resolve("exported_sorted.csv").toFile();
        CSVWriter.exportSortedCSV(exportTarget, data, 2, sortedGrades);

        assertTrue(exportTarget.exists());
        assertTrue(exportTarget.length() > 0);

        CSVData reloaded = CSVReader.readCSV(exportTarget);
        assertEquals(3, reloaded.getHeaders().size());
        assertEquals("Student, Full Name", reloaded.getHeaders().get(1));
        assertEquals(4, reloaded.getRowCount(), "All rows must be preserved in export");

        // First row should be Charlie (78.0)
        assertEquals("Brown, Charlie", reloaded.getRows().get(0)[1]);
        assertEquals("78.0", reloaded.getRows().get(0)[2]);

        // Last row should be Jane (92.0)
        assertEquals("Smith, Jane", reloaded.getRows().get(3)[1]);
        assertEquals("92.0", reloaded.getRows().get(3)[2]);
    }

    @Test
    @DisplayName("Test CSV Export with Real Test Datasets and Sorting Algorithms")
    void testCSVExportRealDatasets() throws IOException {
        String[] testFiles = {
                "sample_students.csv",
                "iris.csv",
                "sample_sales_500.csv",
                "sample_housing_2000.csv",
                "students_performance.csv"
        };

        for (String fileName : testFiles) {
            File srcFile = new File("test-data", fileName);
            if (!srcFile.exists()) continue;

            CSVData data = CSVReader.readCSV(srcFile);
            assertFalse(data.getNumericColumnIndices().isEmpty(), "File should have numeric columns: " + fileName);

            int colIndex = data.getNumericColumnIndices().get(0);
            double[] originalValues = data.getColumnValuesAsDouble(colIndex);
            double[] sortedValues = originalValues.clone();
            Arrays.sort(sortedValues);

            File exportTarget = tempDir.resolve("sorted_" + fileName).toFile();
            CSVWriter.exportSortedCSV(exportTarget, data, colIndex, sortedValues);

            assertTrue(exportTarget.exists(), "Exported file must exist: " + fileName);
            assertTrue(exportTarget.length() > 0, "Exported file must not be empty: " + fileName);

            CSVData reloaded = CSVReader.readCSV(exportTarget);
            assertEquals(data.getRowCount(), reloaded.getRowCount(), "Row count must match for " + fileName);
            assertEquals(data.getHeaders(), reloaded.getHeaders(), "Headers must match for " + fileName);

            // Verify the column is sorted in the exported file
            double[] exportedCol = reloaded.getColumnValuesAsDouble(colIndex);
            assertArrayEquals(sortedValues, exportedCol, 1e-6, "Exported values must be strictly sorted for " + fileName);
        }
    }

    @Test
    @DisplayName("Test CSV Export Null Arguments and Directory Creation")
    void testCSVExportEdgeCases() {
        assertThrows(IOException.class, () -> CSVWriter.exportSortedCSV(null, null, 0, null));

        File nestedDirFile = tempDir.resolve("nested/sub/dir/exported.csv").toFile();
        List<String[]> rows = Arrays.asList(new String[][]{{"1.0"}});
        CSVData data = new CSVData("test.csv", List.of("A"), rows);
        assertDoesNotThrow(() -> CSVWriter.exportSortedCSV(nestedDirFile, data, 0, new double[]{1.0}));
        assertTrue(nestedDirFile.exists(), "Target parent directory should be created automatically");
    }
}
