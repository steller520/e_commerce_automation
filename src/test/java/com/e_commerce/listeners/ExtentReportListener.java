package com.e_commerce.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.e_commerce.utils.ExtentReportManager;
import com.e_commerce.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG Listener for Extent Reports integration
 * Enhanced with ITestResult and ScreenshotUtil
 */
public class ExtentReportListener implements ITestListener, ISuiteListener {
    
    @Override
    public void onStart(ISuite suite) {
        ExtentReportManager.createInstance();
    }
    
    @Override
    public void onFinish(ISuite suite) {
        ExtentReportManager.flush();
        System.out.println("\n==============================================");
        System.out.println("Extent Report Generated: " + ExtentReportManager.getReportPath());
        System.out.println("==============================================\n");
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String className = result.getTestClass().getRealClass().getSimpleName();
        
        ExtentTest test = ExtentReportManager.createTest(testName, description != null ? description : "");
        
        // Add test metadata using ITestResult
        test.assignAuthor("Automation Team");
        test.assignDevice("Chrome Browser");
        
        // Add test categories
        String[] groups = result.getMethod().getGroups();
        if (groups.length > 0) {
            test.assignCategory(groups);
        }
        
        // Add class name as category
        test.assignCategory(className);
        
        // Log test start time
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(result.getStartMillis()));
        test.info("Test Started: " + startTime);
        test.info("Test Class: " + className);
        test.info("Test Method: " + testName);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.PASS, MarkupHelper.createLabel(
                "Test PASSED: " + result.getMethod().getMethodName(), 
                ExtentColor.GREEN
            ));
            
            // Log execution time using ITestResult
            long duration = result.getEndMillis() - result.getStartMillis();
            test.info("Execution Time: " + duration + " ms");
            
            // Log end time
            String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(result.getEndMillis()));
            test.info("Test Ended: " + endTime);
            
            // Optionally capture success screenshot
            try {
                Object testInstance = result.getInstance();
                WebDriver driver = ScreenshotUtil.getDriverFromTestInstance(testInstance);
                
                if (driver != null) {
                    String base64Screenshot = ScreenshotUtil.captureScreenshotAsBase64(driver);
                    if (base64Screenshot != null) {
                        test.pass("Success Screenshot", 
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                    }
                }
            } catch (Exception e) {
                // Ignore screenshot errors for passed tests
            }
        }
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, MarkupHelper.createLabel(
                "Test FAILED: " + result.getMethod().getMethodName(), 
                ExtentColor.RED
            ));
            
            // Log failure details using ITestResult
            String className = result.getTestClass().getRealClass().getSimpleName();
            String methodName = result.getMethod().getMethodName();
            String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(result.getEndMillis()));
            
            test.fail("Failed Test: " + className + "." + methodName);
            test.fail("Failure Time: " + endTime);
            
            // Log exception using ITestResult
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                test.fail(throwable);
                test.fail("Exception Type: " + throwable.getClass().getName());
                test.fail("Exception Message: " + throwable.getMessage());
                test.fail("Stack Trace: <pre>" + getStackTrace(throwable) + "</pre>");
            }
            
            // Capture screenshot using ScreenshotUtil and ITestResult
            try {
                Object testInstance = result.getInstance();
                WebDriver driver = ScreenshotUtil.getDriverFromTestInstance(testInstance);
                
                if (driver != null) {
                    // Capture and save screenshot to file
                    String screenshotPath = ScreenshotUtil.captureFailureScreenshot(driver, result);
                    
                    // Also embed Base64 screenshot in report
                    String base64Screenshot = ScreenshotUtil.captureScreenshotAsBase64(driver);
                    
                    if (screenshotPath != null) {
                        test.fail("Failure Screenshot (File): " + screenshotPath);
                        test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                    }
                    
                    if (base64Screenshot != null) {
                        test.fail("Failure Screenshot", 
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                    }
                } else {
                    test.warning("WebDriver not available - Cannot capture screenshot");
                }
            } catch (Exception e) {
                test.warning("Failed to capture screenshot: " + e.getMessage());
            }
            
            // Log execution time
            long duration = result.getEndMillis() - result.getStartMillis();
            test.info("Execution Time: " + duration + " ms");
        }
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, MarkupHelper.createLabel(
                "Test SKIPPED: " + result.getMethod().getMethodName(), 
                ExtentColor.ORANGE
            ));
            
            // Log skip details using ITestResult
            String className = result.getTestClass().getRealClass().getSimpleName();
            String skipTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(result.getEndMillis()));
            
            test.skip("Skipped Test: " + className + "." + result.getMethod().getMethodName());
            test.skip("Skip Time: " + skipTime);
            
            if (result.getThrowable() != null) {
                test.skip("Reason: " + result.getThrowable().getMessage());
                test.skip(result.getThrowable());
            }
        }
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.WARNING, "Test failed but within success percentage");
            test.warning("Success Percentage: " + result.getMethod().getSuccessPercentage() + "%");
        }
    }
    
    /**
     * Get formatted stack trace
     */
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
