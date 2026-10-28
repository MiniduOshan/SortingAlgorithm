package com.sortingapp.csv;

import com.sortingapp.model.CSVData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing .csv files into the CSVData model.
 * Handles standard CSV parsing including quoted strings, escaped quotes,
 * multiline records, UTF-8 BOM encoding, and robust error validation.
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

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                throw new IOException("CSV file has no header row or content.");
            }

            // Strip UTF-8 Byte Order Mark (BOM) if present
            if (line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }

            if (line.trim().isEmpty()) {
                throw new IOException("CSV file has no header row or content.");
            }

            // Parse header (accommodating multiline headers if quoted)
            int currentLineNumber = 1;
            StringBuilder recordBuilder = new StringBuilder(line);
            while (countUnescapedQuotes(recordBuilder.toString()) % 2 != 0) {
                String next = br.readLine();
                currentLineNumber++;
                if (next == null) {
                    throw new IOException("Malformed CSV: unclosed quotation mark in header at line 1.");
                }
                recordBuilder.append("\n").append(next);
            }

            headers = parseCSVRecord(recordBuilder.toString());
            boolean allEmptyHeaders = true;
            for (String h : headers) {
                if (!h.isEmpty()) {
                    allEmptyHeaders = false;
                    break;
                }
            }
            if (headers.isEmpty() || allEmptyHeaders) {
                throw new IOException("Could not parse valid headers from the CSV file.");
            }

            // Parse data rows
            while ((line = br.readLine()) != null) {
                currentLineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                int startLine = currentLineNumber;
                recordBuilder = new StringBuilder(line);
                while (countUnescapedQuotes(recordBuilder.toString()) % 2 != 0) {
                    String next = br.readLine();
                    currentLineNumber++;
                    if (next == null) {
                        throw new IOException("Malformed CSV: unclosed quotation mark starting at line " + startLine + ".");
                    }
                    recordBuilder.append("\n").append(next);
                }

                List<String> rowTokens = parseCSVRecord(recordBuilder.toString());
                rows.add(rowTokens.toArray(new String[0]));
            }
        }

        if (rows.isEmpty()) {
            throw new IOException("CSV file contains headers but no data rows.");
        }

        return new CSVData(file.getName(), headers, rows);
    }

    /**
     * Parses a single CSV line accounting for quoted fields and escaped quotes.
     */
    public static List<String> parseCSVLine(String line) {
        return parseCSVRecord(line);
    }

    /**
     * Parses a full CSV record accounting for quoted fields and embedded commas.
     */
    public static List<String> parseCSVRecord(String record) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < record.length(); i++) {
            char c = record.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < record.length() && record.charAt(i + 1) == '"') {
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

    private static int countUnescapedQuotes(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '"') {
                if (i + 1 < s.length() && s.charAt(i + 1) == '"') {
                    i++; // Skip escaped pair ""
                } else {
                    count++;
                }
            }
        }
        return count;
    }
}
