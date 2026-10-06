package com.sortingapp.csv;

import com.sortingapp.model.CSVData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing .csv files into CSVData model.
 * Handles standard CSV parsing including quoted strings and comma delimiters.
 * 
 * Group Assignment Contribution:
 * Member 1: File upload functionality and dataset handling.
 */
public class CSVReader {

    /**
     * Reads a CSV file from disk and parses headers and data rows.
     * 
     * @param file The file to read.
     * @return CSVData containing headers, rows, and numeric column information.
     * @throws IOException If file reading fails or file format is invalid.
     */
    public static CSVData readCSV(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("The selected file does not exist.");
        }
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            throw new IOException("Invalid file format. Please upload a valid .csv file.");
        }
        if (file.length() == 0) {
            throw new IOException("The selected CSV file is empty.");
        }

        List<String> headers = new ArrayList<>();
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                throw new IOException("CSV file has no header row or content.");
            }

            // Parse headers
            headers = parseCSVLine(line);
            if (headers.isEmpty()) {
                throw new IOException("Could not parse headers from the CSV file.");
            }

            // Parse data rows
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                List<String> rowTokens = parseCSVLine(line);
                rows.add(rowTokens.toArray(new String[0]));
            }
        }

        if (rows.isEmpty()) {
            throw new IOException("CSV file contains headers but no data rows.");
        }

        return new CSVData(file.getName(), headers, rows);
    }

    /**
     * Parses a single CSV line accounting for quoted fields.
     */
    public static List<String> parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Toggle quote mode or handle escaped quotes ("")
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++; // Skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }
}
