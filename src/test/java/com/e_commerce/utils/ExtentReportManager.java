package com.e_commerce.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for managing Extent Reports
 */
public class ExtentReportManager {
    
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static String reportPath;
    
    /**
     * Initialize Extent Reports
     */
    public static ExtentReports createInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            reportPath = "test-output/ExtentReport_" + timestamp + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            
            // Configure report
            sparkReporter.config().setDocumentTitle("E-Commerce Automation Report");
            sparkReporter.config().setReportName("Test Execution Report");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // System information
            extent.setSystemInfo("Application", "E-Commerce Checkout");
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Browser", "Chrome");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Tester", "Automation Team");
        }
        
        return extent;
    }
    
    /**
     * Get Extent Reports instance
     */
    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }
    
    /**
     * Create a test in the report
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = getInstance().createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }
    
    /**
     * Get current test
     */
    public static ExtentTest getTest() {
        return test.get();
    }
    
    /**
     * Remove current test
     */
    public static void removeTest() {
        test.remove();
    }
    
    /**
     * Flush report
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
    
    /**
     * Get report file path
     */
    public static String getReportPath() {
        return reportPath;
    }
}
