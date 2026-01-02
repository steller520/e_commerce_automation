package com.e_commerce.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for capturing screenshots
 * C6 Project: Enhanced screenshot capture with multiple formats
 */
public class ScreenshotUtil {
    
    private static final String SCREENSHOT_DIR = "test-output/screenshots/";
    private static final String FAILED_SCREENSHOT_DIR = "test-output/screenshots/failed/";
    private static final String PASSED_SCREENSHOT_DIR = "test-output/screenshots/passed/";
    
    /**
     * Capture screenshot on test failure
     * @param driver WebDriver instance
     * @param result ITestResult containing test information
     * @return Screenshot file path
     */
    public static String captureFailureScreenshot(WebDriver driver, ITestResult result) {
        if (driver == null) {
            System.out.println("Cannot capture screenshot - WebDriver is null");
            return null;
        }
        
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        
        String fileName = className + "_" + testName + "_FAILED_" + timestamp + ".png";
        return captureScreenshot(driver, FAILED_SCREENSHOT_DIR, fileName);
    }
    
    /**
     * Capture screenshot on test success (optional)
     * @param driver WebDriver instance
     * @param result ITestResult containing test information
     * @return Screenshot file path
     */
    public static String captureSuccessScreenshot(WebDriver driver, ITestResult result) {
        if (driver == null) {
            return null;
        }
        
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        
        String fileName = className + "_" + testName + "_PASSED_" + timestamp + ".png";
        return captureScreenshot(driver, PASSED_SCREENSHOT_DIR, fileName);
    }
    
    /**
     * Capture screenshot with custom name
     * @param driver WebDriver instance
     * @param testName Test name for file
     * @return Screenshot file path
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            return null;
        }
        
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        return captureScreenshot(driver, SCREENSHOT_DIR, fileName);
    }
    
    /**
     * Internal method to capture screenshot
     * @param driver WebDriver instance
     * @param directory Directory to save screenshot
     * @param fileName File name
     * @return Screenshot file path
     */
    private static String captureScreenshot(WebDriver driver, String directory, String fileName) {
        try {
            // Create directory if it doesn't exist
            Files.createDirectories(Paths.get(directory));
            
            // Capture screenshot
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File source = screenshot.getScreenshotAs(OutputType.FILE);
            
            // Save to file
            String filePath = directory + fileName;
            Files.copy(source.toPath(), Paths.get(filePath));
            
            System.out.println("Screenshot captured: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Capture screenshot as Base64 string (for embedding in reports)
     * @param driver WebDriver instance
     * @return Base64 encoded screenshot
     */
    public static String captureScreenshotAsBase64(WebDriver driver) {
        if (driver == null) {
            return null;
        }
        
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);
            return Base64.getEncoder().encodeToString(screenshotBytes);
        } catch (Exception e) {
            System.err.println("Failed to capture Base64 screenshot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get WebDriver from test instance using reflection
     * @param testInstance Test class instance
     * @return WebDriver instance or null
     */
    public static WebDriver getDriverFromTestInstance(Object testInstance) {
        try {
            // Try to get driver from BaseTest superclass
            java.lang.reflect.Field field = testInstance.getClass().getSuperclass().getDeclaredField("driver");
            field.setAccessible(true);
            return (WebDriver) field.get(testInstance);
        } catch (NoSuchFieldException e) {
            // Try direct field access
            try {
                java.lang.reflect.Field field = testInstance.getClass().getDeclaredField("driver");
                field.setAccessible(true);
                return (WebDriver) field.get(testInstance);
            } catch (Exception ex) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get screenshot directory path
     * @return Screenshot directory
     */
    public static String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }
    
    /**
     * Get failed screenshot directory path
     * @return Failed screenshot directory
     */
    public static String getFailedScreenshotDirectory() {
        return FAILED_SCREENSHOT_DIR;
    }
    
    /**
     * Get passed screenshot directory path
     * @return Passed screenshot directory
     */
    public static String getPassedScreenshotDirectory() {
        return PASSED_SCREENSHOT_DIR;
    }
}
