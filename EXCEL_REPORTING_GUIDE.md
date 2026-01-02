# Excel Test Case Report - Usage Examples

## Overview
The ExcelUtil class provides comprehensive Excel reporting capabilities for test case documentation and execution tracking.

## Features
- ✅ Write test cases with name, steps, expected result, actual result, and status
- ✅ Automatic color coding (Green=PASS, Orange=FAIL, Yellow=SKIP)
- ✅ Test summary with pass rate calculation
- ✅ Read test cases from existing Excel files
- ✅ Update test results dynamically
- ✅ Styled headers and formatted columns

## Manual Usage

### Basic Example - Write Test Cases
```java
// Create Excel report
ExcelUtil excelUtil = new ExcelUtil("reports/test_results.xlsx");
excelUtil.createTestCaseHeader();

// Write single test case
excelUtil.writeTestCase(
    "TC001 - Login with Valid Credentials",
    "1. Navigate to login page\n2. Enter email\n3. Enter password\n4. Click login",
    "User should be logged in successfully",
    "User logged in and redirected to dashboard",
    "PASS"
);

// Write multiple test cases
List<Map<String, String>> testCases = new ArrayList<>();
Map<String, String> tc1 = new LinkedHashMap<>();
tc1.put("name", "TC002 - Add to Cart");
tc1.put("steps", "1. Select product\n2. Click add to cart");
tc1.put("expected", "Product added to cart");
tc1.put("actual", "Product added successfully");
tc1.put("status", "PASS");
testCases.add(tc1);

excelUtil.writeTestCases(testCases);

// Add summary and save
excelUtil.addTestSummary();
excelUtil.save();
```

### Read and Update Results
```java
// Read existing test cases
ExcelUtil excelUtil = new ExcelUtil("reports/test_results.xlsx");
List<Map<String, String>> testCases = excelUtil.readTestCases();

// Update specific test result
excelUtil.updateTestResult("TC001 - Login", "Login failed - timeout", "FAIL");
excelUtil.save();
```

## Automatic Reporting with TestNG Listener

### Enable in testng.xml
Already configured in your testng.xml:
```xml
<listeners>
  <listener class-name="com.e_commerce.listeners.TestResultListener"/>
</listeners>
```

### How It Works
- **Automatic Capture**: All test results are automatically written to Excel
- **Timestamped Reports**: Each run creates a new report with timestamp
- **Auto Summary**: Test summary is generated after suite completion
- **Report Location**: `src/test/resources/test_reports/execution_report_YYYYMMDD_HHMMSS.xlsx`

### Test Result Format
| Test Case Name | Steps | Expected Result | Actual Result | Status |
|----------------|-------|-----------------|---------------|--------|
| TC_CHECKOUT_001 | 1. Login\n2. Add product\n3. Checkout | Order placed successfully | Order placed, payment confirmed | PASS |
| TC_COUPON_001 | Apply invalid coupon | Error message shown | Error displayed correctly | PASS |
| TC_CART_001 | Refresh during checkout | Cart persisted | Cart items lost | FAIL |

## Sample Test Cases

### Checkout Flow Test Case
```java
excelUtil.writeTestCase(
    "TC_CHECKOUT_001 - Complete Checkout Flow",
    "1. Login with test@example.com\n" +
    "2. Navigate to products page\n" +
    "3. Add 'Blue Top' to cart\n" +
    "4. Proceed to checkout\n" +
    "5. Apply coupon 'SAVE10'\n" +
    "6. Enter shipping address\n" +
    "7. Select payment method\n" +
    "8. Confirm order",
    "Order should be placed successfully with 10% discount applied",
    "Order #12345 placed successfully, discount applied, confirmation email sent",
    "PASS"
);
```

### Failed Test Case
```java
excelUtil.writeTestCase(
    "TC_CART_002 - Cart Persistence After Refresh",
    "1. Add product to cart\n" +
    "2. Refresh browser\n" +
    "3. Verify cart contents",
    "Cart should retain all products after refresh",
    "AssertionError: Expected cart count=1, but was 0. Cart items lost after refresh.",
    "FAIL"
);
```

## Benefits
1. **Professional Documentation**: Create standardized test case documents
2. **Traceability**: Track expected vs actual results for each test
3. **Reporting**: Generate executive-ready test execution reports
4. **Historical Data**: Keep track of test execution history
5. **Integration Ready**: Works seamlessly with TestNG framework
6. **No Manual Work**: Automatic report generation with listener

## Report Output Location
All Excel reports are saved to:
```
src/test/resources/test_reports/
├── execution_report_20260102_143022.xlsx
├── execution_report_20260102_154511.xlsx
└── test_cases.xlsx
```

## Color Legend
- 🟢 **Green (PASS)**: Test executed successfully
- 🟠 **Orange (FAIL)**: Test failed with errors
- 🟡 **Yellow (SKIP)**: Test was skipped/not executed
