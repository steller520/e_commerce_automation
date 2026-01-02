package com.e_commerce.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class for reading and writing Excel files for test case management.
 * Supports writing test cases with name, steps, expected result, and actual result.
 */
public class ExcelUtil {
    
    private Workbook workbook;
    private Sheet sheet;
    private String filePath;
    
    /**
     * Create new Excel workbook with default sheet name
     * @param filePath Path where Excel file will be saved
     */
    public ExcelUtil(String filePath) {
        this(filePath, "Test Cases");
    }
    
    /**
     * Create new Excel workbook with custom sheet name
     * @param filePath Path where Excel file will be saved
     * @param sheetName Name of the sheet
     */
    public ExcelUtil(String filePath, String sheetName) {
        this.filePath = filePath;
        
        File file = new File(filePath);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    sheet = workbook.createSheet(sheetName);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error loading existing Excel file: " + e.getMessage(), e);
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(sheetName);
        }
    }
    
    /**
     * Create test case header row with styling
     * Headers: Test Case Name | Steps | Expected Result | Actual Result | Status
     */
    public void createTestCaseHeader() {
        Row headerRow = sheet.createRow(0);
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        String[] headers = {"Test Case Name", "Steps", "Expected Result", "Actual Result", "Status"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Set column widths
        sheet.setColumnWidth(0, 8000);  // Test Case Name
        sheet.setColumnWidth(1, 12000); // Steps
        sheet.setColumnWidth(2, 8000);  // Expected Result
        sheet.setColumnWidth(3, 8000);  // Actual Result
        sheet.setColumnWidth(4, 4000);  // Status
    }
    
    /**
     * Write a single test case to Excel
     * @param testCaseName Name of the test case
     * @param steps Test steps (can be multi-line, separated by newline)
     * @param expectedResult Expected outcome
     * @param actualResult Actual outcome
     * @param status Test status (PASS/FAIL/SKIP)
     */
    public void writeTestCase(String testCaseName, String steps, String expectedResult, 
                              String actualResult, String status) {
        
        int lastRowNum = sheet.getLastRowNum();
        int newRowNum = (lastRowNum == 0 && sheet.getRow(0) == null) ? 0 : lastRowNum + 1;
        
        Row row = sheet.createRow(newRowNum);
        
        // Create cell styles
        CellStyle normalStyle = createNormalCellStyle();
        CellStyle statusStyle = createStatusCellStyle(status);
        
        // Test Case Name
        Cell nameCell = row.createCell(0);
        nameCell.setCellValue(testCaseName);
        nameCell.setCellStyle(normalStyle);
        
        // Steps
        Cell stepsCell = row.createCell(1);
        stepsCell.setCellValue(steps);
        stepsCell.setCellStyle(normalStyle);
        
        // Expected Result
        Cell expectedCell = row.createCell(2);
        expectedCell.setCellValue(expectedResult);
        expectedCell.setCellStyle(normalStyle);
        
        // Actual Result
        Cell actualCell = row.createCell(3);
        actualCell.setCellValue(actualResult);
        actualCell.setCellStyle(normalStyle);
        
        // Status
        Cell statusCell = row.createCell(4);
        statusCell.setCellValue(status);
        statusCell.setCellStyle(statusStyle);
    }
    
    /**
     * Write multiple test cases at once
     * @param testCases List of test case data (Map with keys: name, steps, expected, actual, status)
     */
    public void writeTestCases(List<Map<String, String>> testCases) {
        for (Map<String, String> testCase : testCases) {
            writeTestCase(
                testCase.get("name"),
                testCase.get("steps"),
                testCase.get("expected"),
                testCase.get("actual"),
                testCase.get("status")
            );
        }
    }
    
    /**
     * Read test cases from Excel file
     * @return List of test case maps
     */
    public List<Map<String, String>> readTestCases() {
        List<Map<String, String>> testCases = new ArrayList<>();
        
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 1) {
            return testCases; // No data rows
        }
        
        // Read header to get column names
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }
        
        // Read data rows
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            Map<String, String> testCase = new LinkedHashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                String value = getCellValueAsString(cell);
                testCase.put(headers.get(j), value);
            }
            testCases.add(testCase);
        }
        
        return testCases;
    }
    
    /**
     * Update test result for a specific test case
     * @param testCaseName Name of the test case to update
     * @param actualResult Actual result to set
     * @param status Status to set (PASS/FAIL/SKIP)
     * @return true if test case was found and updated, false otherwise
     */
    public boolean updateTestResult(String testCaseName, String actualResult, String status) {
        int lastRowNum = sheet.getLastRowNum();
        
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            Cell nameCell = row.getCell(0);
            if (nameCell != null && testCaseName.equals(nameCell.getStringCellValue())) {
                // Update actual result
                Cell actualCell = row.getCell(3);
                if (actualCell == null) {
                    actualCell = row.createCell(3);
                }
                actualCell.setCellValue(actualResult);
                actualCell.setCellStyle(createNormalCellStyle());
                
                // Update status
                Cell statusCell = row.getCell(4);
                if (statusCell == null) {
                    statusCell = row.createCell(4);
                }
                statusCell.setCellValue(status);
                statusCell.setCellStyle(createStatusCellStyle(status));
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Add test summary at the end of the sheet
     */
    public void addTestSummary() {
        int lastRowNum = sheet.getLastRowNum();
        int summaryRowNum = lastRowNum + 3;
        
        // Count test results
        int total = 0, passed = 0, failed = 0, skipped = 0;
        
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            Cell statusCell = row.getCell(4);
            if (statusCell != null) {
                total++;
                String status = statusCell.getStringCellValue();
                if ("PASS".equalsIgnoreCase(status)) passed++;
                else if ("FAIL".equalsIgnoreCase(status)) failed++;
                else if ("SKIP".equalsIgnoreCase(status)) skipped++;
            }
        }
        
        // Create summary
        CellStyle summaryLabelStyle = workbook.createCellStyle();
        Font summaryFont = workbook.createFont();
        summaryFont.setBold(true);
        summaryLabelStyle.setFont(summaryFont);
        
        Row summaryHeaderRow = sheet.createRow(summaryRowNum);
        Cell summaryHeaderCell = summaryHeaderRow.createCell(0);
        summaryHeaderCell.setCellValue("TEST SUMMARY");
        summaryHeaderCell.setCellStyle(summaryLabelStyle);
        
        String[][] summaryData = {
            {"Total Tests", String.valueOf(total)},
            {"Passed", String.valueOf(passed)},
            {"Failed", String.valueOf(failed)},
            {"Skipped", String.valueOf(skipped)},
            {"Pass Rate", total > 0 ? String.format("%.2f%%", (passed * 100.0 / total)) : "0%"}
        };
        
        for (int i = 0; i < summaryData.length; i++) {
            Row row = sheet.createRow(summaryRowNum + 1 + i);
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(summaryData[i][0]);
            labelCell.setCellStyle(summaryLabelStyle);
            
            Cell valueCell = row.createCell(1);
            valueCell.setCellValue(summaryData[i][1]);
        }
    }
    
    /**
     * Save and close the workbook
     */
    public void save() {
        try {
            // Create directories if they don't exist
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Error saving Excel file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create normal cell style with borders
     */
    private CellStyle createNormalCellStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        return style;
    }
    
    /**
     * Create status cell style with color based on status
     */
    private CellStyle createStatusCellStyle(String status) {
        CellStyle style = createNormalCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        
        if ("PASS".equalsIgnoreCase(status)) {
            style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if ("FAIL".equalsIgnoreCase(status)) {
            style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if ("SKIP".equalsIgnoreCase(status)) {
            style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        
        return style;
    }
    
    /**
     * Get cell value as string regardless of cell type
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    /**
     * Get current workbook
     */
    public Workbook getWorkbook() {
        return workbook;
    }
    
    /**
     * Get current sheet
     */
    public Sheet getSheet() {
        return sheet;
    }
}
