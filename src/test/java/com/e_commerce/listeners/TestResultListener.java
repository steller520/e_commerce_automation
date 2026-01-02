package com.e_commerce.listeners;

import com.e_commerce.utils.ExcelUtil;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG listener to automatically capture test results to Excel file
 */
public class TestResultListener implements ITestListener, ISuiteListener {
    
    private ExcelUtil excelUtil;
    private String reportPath;
    private int testCount = 0;
    
    @Override
    public void onStart(ISuite suite) {
        // Create Excel report when suite starts
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        reportPath = "src/test/resources/test_reports/execution_report_" + timestamp + ".xlsx";
        
        excelUtil = new ExcelUtil(reportPath, suite.getName());
        excelUtil.createTestCaseHeader();
        
        System.out.println("Test execution report will be saved to: " + reportPath);
    }
    
    @Override
    public void onFinish(ISuite suite) {
        // Add summary and save when suite finishes
        if (excelUtil != null) {
            excelUtil.addTestSummary();
            excelUtil.save();
            System.out.println("Test execution report saved: " + reportPath);
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        testCount++;
        System.out.println("Starting test: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        writeTestResult(result, "PASS");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        writeTestResult(result, "FAIL");
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        writeTestResult(result, "SKIP");
    }
    
    private void writeTestResult(ITestResult result, String status) {
        String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        
        // Get test steps from description or method name
        String steps = description != null && !description.isEmpty() ? description : "Automated test execution";
        
        // Expected result
        String expected = "Test should " + (status.equals("PASS") ? "pass" : "complete") + " without errors";
        
        // Actual result
        String actual;
        if (status.equals("PASS")) {
            actual = "Test executed successfully";
        } else if (status.equals("FAIL")) {
            Throwable throwable = result.getThrowable();
            actual = throwable != null ? throwable.getMessage() : "Test failed";
        } else {
            actual = "Test skipped";
        }
        
        // Write to Excel
        if (excelUtil != null) {
            excelUtil.writeTestCase(testName, steps, expected, actual, status);
        }
    }
}
