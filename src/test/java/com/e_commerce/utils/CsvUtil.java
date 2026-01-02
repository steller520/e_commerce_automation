package com.e_commerce.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class for reading and writing CSV files for test data management.
 * Supports custom delimiters, headers, and data conversion.
 */
public class CsvUtil {
    
    private String delimiter;
    
    /**
     * Constructor with default comma delimiter
     */
    public CsvUtil() {
        this.delimiter = ",";
    }
    
    /**
     * Constructor with custom delimiter
     * @param delimiter Custom delimiter (e.g., ";", "|", "\t")
     */
    public CsvUtil(String delimiter) {
        this.delimiter = delimiter;
    }
    
    /**
     * Read CSV file and return data as List of Maps (header -> value)
     * @param filePath Absolute or relative path to CSV file
     * @return List of Maps where each map represents a row with column headers as keys
     * @throws IOException If file cannot be read
     */
    public List<Map<String, String>> readCsvAsMapList(String filePath) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        Path path = Paths.get(filePath);
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return data; // Empty file
            }
            
            String[] headers = parseLine(headerLine);
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] values = parseLine(line);
                Map<String, String> row = new LinkedHashMap<>();
                
                for (int i = 0; i < headers.length; i++) {
                    String value = i < values.length ? values[i] : "";
                    row.put(headers[i].trim(), value.trim());
                }
                
                data.add(row);
            }
        }
        
        return data;
    }
    
    /**
     * Read CSV file and return data as 2D array
     * @param filePath Path to CSV file
     * @param skipHeader Whether to skip the first row (header)
     * @return 2D array of strings
     * @throws IOException If file cannot be read
     */
    public String[][] readCsvAsArray(String filePath, boolean skipHeader) throws IOException {
        List<String[]> rows = new ArrayList<>();
        Path path = Paths.get(filePath);
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine && skipHeader) {
                    firstLine = false;
                    continue;
                }
                rows.add(parseLine(line));
                firstLine = false;
            }
        }
        
        return rows.toArray(new String[0][]);
    }
    
    /**
     * Read CSV file for TestNG DataProvider
     * @param filePath Path to CSV file
     * @return Object[][] suitable for TestNG @DataProvider
     * @throws IOException If file cannot be read
     */
    public Object[][] readCsvForDataProvider(String filePath) throws IOException {
        String[][] data = readCsvAsArray(filePath, true);
        Object[][] objectData = new Object[data.length][];
        
        for (int i = 0; i < data.length; i++) {
            objectData[i] = new Object[data[i].length];
            System.arraycopy(data[i], 0, objectData[i], 0, data[i].length);
        }
        
        return objectData;
    }
    
    /**
     * Write data to CSV file from List of Maps
     * @param filePath Path to output CSV file
     * @param data List of maps (each map is a row)
     * @param headers Column headers (if null, uses keys from first map)
     * @throws IOException If file cannot be written
     */
    public void writeCsv(String filePath, List<Map<String, String>> data, List<String> headers) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        
        // Use headers from parameter or first map
        List<String> columnHeaders = headers != null ? headers : new ArrayList<>(data.get(0).keySet());
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Write header
            writer.write(String.join(delimiter, columnHeaders));
            writer.newLine();
            
            // Write data rows
            for (Map<String, String> row : data) {
                List<String> values = new ArrayList<>();
                for (String header : columnHeaders) {
                    String value = row.getOrDefault(header, "");
                    values.add(escapeValue(value));
                }
                writer.write(String.join(delimiter, values));
                writer.newLine();
            }
        }
    }
    
    /**
     * Write data to CSV file from 2D array
     * @param filePath Path to output CSV file
     * @param data 2D array of data
     * @param includeHeader Whether first row is header
     * @throws IOException If file cannot be written
     */
    public void writeCsvFromArray(String filePath, String[][] data, boolean includeHeader) throws IOException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String[] row : data) {
                List<String> escapedValues = new ArrayList<>();
                for (String value : row) {
                    escapedValues.add(escapeValue(value != null ? value : ""));
                }
                writer.write(String.join(delimiter, escapedValues));
                writer.newLine();
            }
        }
    }
    
    /**
     * Append row to existing CSV file
     * @param filePath Path to CSV file
     * @param row Map representing the row to append
     * @throws IOException If file cannot be written
     */
    public void appendRow(String filePath, Map<String, String> row) throws IOException {
        Path path = Paths.get(filePath);
        
        // Read existing headers
        List<String> headers;
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String headerLine = reader.readLine();
                headers = Arrays.asList(parseLine(headerLine));
            }
        } else {
            headers = new ArrayList<>(row.keySet());
            // Create file with headers
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(String.join(delimiter, headers));
                writer.newLine();
            }
        }
        
        // Append row
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            List<String> values = new ArrayList<>();
            for (String header : headers) {
                String value = row.getOrDefault(header, "");
                values.add(escapeValue(value));
            }
            writer.write(String.join(delimiter, values));
            writer.newLine();
        }
    }
    
    /**
     * Parse a single CSV line handling quotes and escape characters
     * @param line CSV line to parse
     * @return Array of values
     */
    private String[] parseLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean insideQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                // Check for escaped quote
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++; // Skip next quote
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (c == delimiter.charAt(0) && !insideQuotes) {
                values.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        values.add(currentValue.toString());
        return values.toArray(new String[0]);
    }
    
    /**
     * Escape value for CSV writing (add quotes if contains delimiter or quotes)
     * @param value Value to escape
     * @return Escaped value
     */
    private String escapeValue(String value) {
        if (value.contains(delimiter) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Get column values from CSV file
     * @param filePath Path to CSV file
     * @param columnName Name of column to extract
     * @return List of values from specified column
     * @throws IOException If file cannot be read
     */
    public List<String> getColumnValues(String filePath, String columnName) throws IOException {
        List<String> columnValues = new ArrayList<>();
        List<Map<String, String>> data = readCsvAsMapList(filePath);
        
        for (Map<String, String> row : data) {
            columnValues.add(row.getOrDefault(columnName, ""));
        }
        
        return columnValues;
    }
    
    /**
     * Count rows in CSV file (excluding header)
     * @param filePath Path to CSV file
     * @return Number of data rows
     * @throws IOException If file cannot be read
     */
    public int countRows(String filePath) throws IOException {
        return readCsvAsMapList(filePath).size();
    }
}
