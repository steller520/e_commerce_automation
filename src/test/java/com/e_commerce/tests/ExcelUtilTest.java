package com.e_commerce.tests;

import com.e_commerce.utils.ExcelUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

/**
 * Unit tests for ExcelUtil class
 */
public class ExcelUtilTest {
    
    private String testExcelPath;
    private String reportExcelPath;
    
    @BeforeClass
    public void setUp() {
        testExcelPath = "src/test/resources/test_reports/test_cases.xlsx";
        reportExcelPath = "src/test/resources/test_reports/test_execution_report.xlsx";
    }
    
    @AfterClass
    public void tearDown() {
        // Clean up test files
        new File(testExcelPath).delete();
        new File(reportExcelPath).delete();
    }
    
    @Test
    public void testCreateExcelWithHeader() {
        ExcelUtil excelUtil = new ExcelUtil(testExcelPath);
        excelUtil.createTestCaseHeader();
        excelUtil.save();
        
        File file = new File(testExcelPath);
        Assert.assertTrue(file.exists(), "Excel file should be created");
        Assert.assertTrue(file.length() > 0, "Excel file should not be empty");
    }
    
    @Test(dependsOnMethods = "testCreateExcelWithHeader")
    public void testWriteSingleTestCase() {
        ExcelUtil excelUtil = new ExcelUtil(testExcelPath);
        
        excelUtil.writeTestCase(
            "TC001 - User Login with Valid Credentials",
            "1. Navigate to login page\n2. Enter valid email\n3. Enter valid password\n4. Click login button\n5. Verify dashboard is displayed",
            "User should be logged in successfully and redirected to dashboard",
            "User logged in successfully",
            "PASS"
        );
        
        excelUtil.save();
        
        // Read back and verify
        ExcelUtil readUtil = new ExcelUtil(testExcelPath);
        List<Map<String, String>> testCases = readUtil.readTestCases();
        
        Assert.assertEquals(testCases.size(), 1, "Should have 1 test case");
        Assert.assertEquals(testCases.get(0).get("Test Case Name"), "TC001 - User Login with Valid Credentials");
        Assert.assertEquals(testCases.get(0).get("Status"), "PASS");
    }
    
    @Test(dependsOnMethods = "testWriteSingleTestCase")
    public void testWriteMultipleTestCases() {
        ExcelUtil excelUtil = new ExcelUtil(testExcelPath);
        
        List<Map<String, String>> testCases = new ArrayList<>();
        
        // Test case 2
        Map<String, String> tc2 = new LinkedHashMap<>();
        tc2.put("name", "TC002 - Add Product to Cart");
        tc2.put("steps", "1. Navigate to products page\n2. Click on first product\n3. Click 'Add to Cart'\n4. Verify cart count increases");
        tc2.put("expected", "Product should be added to cart successfully");
        tc2.put("actual", "Product added to cart");
        tc2.put("status", "PASS");
        testCases.add(tc2);
        
        // Test case 3
        Map<String, String> tc3 = new LinkedHashMap<>();
        tc3.put("name", "TC003 - Apply Invalid Coupon");
        tc3.put("steps", "1. Add product to cart\n2. Go to checkout\n3. Enter invalid coupon code 'INVALID123'\n4. Click apply");
        tc3.put("expected", "Error message should be displayed");
        tc3.put("actual", "Error message not displayed");
        tc3.put("status", "FAIL");
        testCases.add(tc3);
        
        // Test case 4
        Map<String, String> tc4 = new LinkedHashMap<>();
        tc4.put("name", "TC004 - Search Product");
        tc4.put("steps", "1. Navigate to home page\n2. Enter product name in search\n3. Click search button");
        tc4.put("expected", "Matching products should be displayed");
        tc4.put("actual", "Not executed");
        tc4.put("status", "SKIP");
        testCases.add(tc4);
        
        excelUtil.writeTestCases(testCases);
        excelUtil.save();
        
        // Verify
        ExcelUtil readUtil = new ExcelUtil(testExcelPath);
        List<Map<String, String>> allTestCases = readUtil.readTestCases();
        
        Assert.assertEquals(allTestCases.size(), 4, "Should have 4 test cases total");
    }
    
    @Test(dependsOnMethods = "testWriteMultipleTestCases")
    public void testUpdateTestResult() {
        ExcelUtil excelUtil = new ExcelUtil(testExcelPath);
        
        boolean updated = excelUtil.updateTestResult(
            "TC004 - Search Product",
            "Search returned correct results",
            "PASS"
        );
        
        Assert.assertTrue(updated, "Test result should be updated");
        excelUtil.save();
        
        // Verify update
        ExcelUtil readUtil = new ExcelUtil(testExcelPath);
        List<Map<String, String>> testCases = readUtil.readTestCases();
        
        Map<String, String> tc4 = testCases.stream()
            .filter(tc -> tc.get("Test Case Name").equals("TC004 - Search Product"))
            .findFirst()
            .orElse(null);
        
        Assert.assertNotNull(tc4, "Test case should exist");
        Assert.assertEquals(tc4.get("Actual Result"), "Search returned correct results");
        Assert.assertEquals(tc4.get("Status"), "PASS");
    }
    
    @Test(dependsOnMethods = "testUpdateTestResult")
    public void testAddTestSummary() {
        ExcelUtil excelUtil = new ExcelUtil(testExcelPath);
        excelUtil.addTestSummary();
        excelUtil.save();
        
        // Verify file is saved
        File file = new File(testExcelPath);
        Assert.assertTrue(file.exists(), "Excel file with summary should exist");
    }
    
    @Test
    public void testCompleteTestExecutionReport() {
        ExcelUtil excelUtil = new ExcelUtil(reportExcelPath, "Checkout Tests");
        excelUtil.createTestCaseHeader();
        
        // Create comprehensive test report
        List<Map<String, String>> testCases = new ArrayList<>();
        
        // Happy path checkout
        Map<String, String> tc1 = new LinkedHashMap<>();
        tc1.put("name", "TC_CHECKOUT_001 - Complete Checkout Flow");
        tc1.put("steps", "1. Login with valid credentials\n" +
                        "2. Navigate to products page\n" +
                        "3. Add product 'Blue Top' to cart\n" +
                        "4. Proceed to checkout\n" +
                        "5. Apply coupon 'SAVE10'\n" +
                        "6. Verify discount applied\n" +
                        "7. Enter shipping address\n" +
                        "8. Select payment method\n" +
                        "9. Confirm order");
        tc1.put("expected", "Order should be placed successfully with 10% discount");
        tc1.put("actual", "Order placed successfully, discount applied");
        tc1.put("status", "PASS");
        testCases.add(tc1);
        
        // Coupon validation
        Map<String, String> tc2 = new LinkedHashMap<>();
        tc2.put("name", "TC_COUPON_001 - Apply Invalid Coupon");
        tc2.put("steps", "1. Add product to cart\n" +
                        "2. Proceed to checkout\n" +
                        "3. Enter coupon code 'INVALID123'\n" +
                        "4. Click apply button\n" +
                        "5. Verify error message");
        tc2.put("expected", "Error message: 'Invalid coupon code' should be displayed");
        tc2.put("actual", "Error message displayed correctly");
        tc2.put("status", "PASS");
        testCases.add(tc2);
        
        // Cart persistence
        Map<String, String> tc3 = new LinkedHashMap<>();
        tc3.put("name", "TC_CART_001 - Cart Persistence After Refresh");
        tc3.put("steps", "1. Add product to cart\n" +
                        "2. Refresh browser\n" +
                        "3. Verify cart still contains product");
        tc3.put("expected", "Cart should retain products after page refresh");
        tc3.put("actual", "Cart items lost after refresh");
        tc3.put("status", "FAIL");
        testCases.add(tc3);
        
        // Database validation
        Map<String, String> tc4 = new LinkedHashMap<>();
        tc4.put("name", "TC_DB_001 - Order Payment Consistency");
        tc4.put("steps", "1. Place an order\n" +
                        "2. Query orders table\n" +
                        "3. Query payments table\n" +
                        "4. Verify order_id matches in both tables");
        tc4.put("expected", "Every order should have corresponding payment record");
        tc4.put("actual", "All orders have matching payment records");
        tc4.put("status", "PASS");
        testCases.add(tc4);
        
        // Navigation test
        Map<String, String> tc5 = new LinkedHashMap<>();
        tc5.put("name", "TC_NAV_001 - Back Button During Checkout");
        tc5.put("steps", "1. Add product to cart\n" +
                        "2. Proceed to checkout\n" +
                        "3. Click browser back button\n" +
                        "4. Verify cart contents preserved");
        tc5.put("expected", "Cart should preserve items when navigating back");
        tc5.put("actual", "Pending execution");
        tc5.put("status", "SKIP");
        testCases.add(tc5);
        
        excelUtil.writeTestCases(testCases);
        excelUtil.addTestSummary();
        excelUtil.save();
        
        // Verify
        Assert.assertTrue(new File(reportExcelPath).exists(), "Report file should be created");
    }
    
    @Test
    public void testReadTestCases() {
        // Create test data
        ExcelUtil writeUtil = new ExcelUtil("src/test/resources/test_reports/read_test.xlsx");
        writeUtil.createTestCaseHeader();
        writeUtil.writeTestCase("Test 1", "Step 1", "Expected 1", "Actual 1", "PASS");
        writeUtil.writeTestCase("Test 2", "Step 2", "Expected 2", "Actual 2", "FAIL");
        writeUtil.save();
        
        // Read and verify
        ExcelUtil readUtil = new ExcelUtil("src/test/resources/test_reports/read_test.xlsx");
        List<Map<String, String>> testCases = readUtil.readTestCases();
        
        Assert.assertEquals(testCases.size(), 2, "Should read 2 test cases");
        Assert.assertEquals(testCases.get(0).get("Test Case Name"), "Test 1");
        Assert.assertEquals(testCases.get(1).get("Status"), "FAIL");
        
        // Cleanup
        new File("src/test/resources/test_reports/read_test.xlsx").delete();
    }
}
