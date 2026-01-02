package com.e_commerce.tests;

import com.e_commerce.utils.CsvUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Unit tests for CsvUtil class
 */
public class CsvUtilTest {
    
    private CsvUtil csvUtil;
    private String testCsvPath;
    private String outputCsvPath;
    
    @BeforeClass
    public void setUp() throws IOException {
        csvUtil = new CsvUtil();
        testCsvPath = "src/test/resources/testdata/products.csv";
        outputCsvPath = "src/test/resources/testdata/output.csv";
        
        // Create test CSV file
        createTestCsvFile();
    }
    
    @AfterClass
    public void tearDown() throws IOException {
        // Clean up output files
        Files.deleteIfExists(Paths.get(outputCsvPath));
    }
    
    private void createTestCsvFile() throws IOException {
        Path path = Paths.get(testCsvPath);
        Files.createDirectories(path.getParent());
        
        String csvContent = "ProductName,Price,Category,InStock\n" +
                           "Blue Top,500,Women,true\n" +
                           "Men Tshirt,400,Men,true\n" +
                           "Sleeveless Dress,1000,Women,false\n" +
                           "Printed Dress,600,Women,true\n" +
                           "Denim Jeans,800,Men,true\n";
        
        Files.write(path, csvContent.getBytes());
    }
    
    @Test
    public void testReadCsvAsMapList() throws IOException {
        List<Map<String, String>> data = csvUtil.readCsvAsMapList(testCsvPath);
        
        Assert.assertEquals(data.size(), 5, "Should read 5 rows");
        Assert.assertEquals(data.get(0).get("ProductName"), "Blue Top");
        Assert.assertEquals(data.get(0).get("Price"), "500");
        Assert.assertEquals(data.get(0).get("Category"), "Women");
        Assert.assertEquals(data.get(0).get("InStock"), "true");
    }
    
    @Test
    public void testReadCsvAsArray() throws IOException {
        String[][] data = csvUtil.readCsvAsArray(testCsvPath, true);
        
        Assert.assertEquals(data.length, 5, "Should read 5 data rows");
        Assert.assertEquals(data[0][0], "Blue Top");
        Assert.assertEquals(data[1][0], "Men Tshirt");
    }
    
    @Test
    public void testReadCsvForDataProvider() throws IOException {
        Object[][] data = csvUtil.readCsvForDataProvider(testCsvPath);
        
        Assert.assertEquals(data.length, 5, "Should read 5 rows for data provider");
        Assert.assertEquals(data[0].length, 4, "Each row should have 4 columns");
        Assert.assertEquals(data[0][0], "Blue Top");
    }
    
    @Test
    public void testWriteCsv() throws IOException {
        // Create test data
        List<Map<String, String>> data = new ArrayList<>();
        
        Map<String, String> row1 = new LinkedHashMap<>();
        row1.put("TestCase", "Login Valid User");
        row1.put("Username", "test@example.com");
        row1.put("Password", "password123");
        row1.put("ExpectedResult", "Success");
        
        Map<String, String> row2 = new LinkedHashMap<>();
        row2.put("TestCase", "Login Invalid User");
        row2.put("Username", "invalid@example.com");
        row2.put("Password", "wrong");
        row2.put("ExpectedResult", "Failure");
        
        data.add(row1);
        data.add(row2);
        
        // Write CSV
        csvUtil.writeCsv(outputCsvPath, data, null);
        
        // Verify written data
        List<Map<String, String>> readData = csvUtil.readCsvAsMapList(outputCsvPath);
        Assert.assertEquals(readData.size(), 2);
        Assert.assertEquals(readData.get(0).get("TestCase"), "Login Valid User");
        Assert.assertEquals(readData.get(1).get("Username"), "invalid@example.com");
    }
    
    @Test
    public void testGetColumnValues() throws IOException {
        List<String> productNames = csvUtil.getColumnValues(testCsvPath, "ProductName");
        
        Assert.assertEquals(productNames.size(), 5);
        Assert.assertTrue(productNames.contains("Blue Top"));
        Assert.assertTrue(productNames.contains("Denim Jeans"));
    }
    
    @Test
    public void testCountRows() throws IOException {
        int rowCount = csvUtil.countRows(testCsvPath);
        Assert.assertEquals(rowCount, 5, "Should count 5 data rows");
    }
    
    @Test
    public void testAppendRow() throws IOException {
        String appendTestPath = "src/test/resources/testdata/append_test.csv";
        
        // Create initial CSV
        List<Map<String, String>> initialData = new ArrayList<>();
        Map<String, String> row1 = new LinkedHashMap<>();
        row1.put("ID", "1");
        row1.put("Name", "John");
        initialData.add(row1);
        csvUtil.writeCsv(appendTestPath, initialData, null);
        
        // Append new row
        Map<String, String> newRow = new LinkedHashMap<>();
        newRow.put("ID", "2");
        newRow.put("Name", "Jane");
        csvUtil.appendRow(appendTestPath, newRow);
        
        // Verify
        List<Map<String, String>> allData = csvUtil.readCsvAsMapList(appendTestPath);
        Assert.assertEquals(allData.size(), 2);
        Assert.assertEquals(allData.get(1).get("Name"), "Jane");
        
        // Clean up
        Files.deleteIfExists(Paths.get(appendTestPath));
    }
    
    @Test
    public void testCsvWithQuotesAndCommas() throws IOException {
        String specialCharsPath = "src/test/resources/testdata/special_chars.csv";
        
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> row = new LinkedHashMap<>();
        row.put("Description", "Product with comma, quotes \"test\", and newline");
        row.put("Price", "1,234.56");
        data.add(row);
        
        csvUtil.writeCsv(specialCharsPath, data, null);
        
        List<Map<String, String>> readData = csvUtil.readCsvAsMapList(specialCharsPath);
        Assert.assertEquals(readData.get(0).get("Description"), "Product with comma, quotes \"test\", and newline");
        
        Files.deleteIfExists(Paths.get(specialCharsPath));
    }
    
    @DataProvider(name = "productData")
    public Object[][] getProductData() throws IOException {
        return csvUtil.readCsvForDataProvider(testCsvPath);
    }
    
    @Test(dataProvider = "productData")
    public void testDataDrivenWithCsv(String productName, String price, String category, String inStock) {
        System.out.println("Testing product: " + productName + " | Price: " + price + " | Category: " + category);
        Assert.assertNotNull(productName, "Product name should not be null");
        Assert.assertNotNull(price, "Price should not be null");
    }
}
