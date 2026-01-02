
# C6 Project – End-to-End QA Simulation for an E-Commerce Application

## Project Overview
An e-commerce business is facing intermittent failures and drop-offs during checkout.

### Reported Issues
- Cart items disappearing or quantities changing
- Coupon codes not applying correctly
- Payments debited but orders not created
- Incorrect address validations and shipping charges
- Users logged out or losing cart mid-checkout
- UI and DB data inconsistencies
- Orphan records (payment without order / order without payment)
- Duplicate orders or payments
- Incorrect stock levels due to race conditions

You are hired as a **Quality Analyst** to design and implement a complete test strategy and automation suite covering **UI, API, and Database validations**.

---

## Project Objectives
1. Design a full checkout test strategy
2. Create test cases for happy, edge, and negative paths
3. Build automation using Selenium (Java) and Postman
4. Perform SQL-based DB validations
5. Validate payment gateway integrations (mocked)
6. Ensure session and data consistency
7. Produce professional QA documentation

---

## Application Under Test – Functional Overview

### 1. User & Access
- Guest checkout
- Login / Signup
- User profile with addresses and orders

### 2. Product & Cart
- Product listing, search, filters
- Product details
- Cart operations
- Real-time pricing updates

### 3. Checkout Steps
1. Cart Review
2. Address Selection
3. Shipping Method
4. Payment
5. Order Confirmation

### 4. Additional Flows
- My Orders
- Out-of-stock handling
- Cart persistence

---

## Database Perspective – Conceptual Schema
Entities:
- users, addresses, products
- carts, cart_items
- orders, order_items
- payments
- coupons, coupon_usage
- shipments

---

## Database Setup
- Database: `ecommerce_checkout_db`
- RDBMS: PostgreSQL / MySQL

### Tables
- Users
- Addresses
- Products
- Carts
- Cart Items
- Coupons
- Coupon Usage
- Orders
- Order Items
- Payments

---

## Insert Sample Data
Includes sample users, addresses, products, and coupons.

---

## Simulate Successful Checkout (DB Level)
1. Create cart
2. Add items
3. Calculate subtotal
4. Create order
5. Move cart items → order items
6. Reduce stock
7. Record coupon usage
8. Simulate payment success

---

## Scope of Testing

### 1. Functional Testing
- Cart Management
- Coupons & Discounts
- Address Validation
- Shipping Options
- Payment Flows
- Order Confirmation

### 2. Database & SQL Testing
- Data integrity
- Cart & session data
- Stock validation
- Coupon rules
- Transactions & rollbacks
- Referential integrity

---

## Tools & Technologies
- Java
- Selenium WebDriver
- Postman
- PostgreSQL / MySQL
- TestNG
- Git & GitHub
- Jira

---

## Project Phases & Deliverables
1. Requirement Analysis & Test Planning
2. Test Design & Test Data
3. Manual Execution & Bug Reporting
4. Automation Framework Design
5. Test Automation Development

---

## Sample High-Impact Scenarios
- Payment success but order not created
- Double payment prevention
- Concurrent cart updates
- Stock change mid-checkout
- Cross-browser consistency

---

## Demo AUTs
- https://automationexercise.com
- https://demo.opencart.com
- https://www.saucedemo.com

---

## Evaluation Rubrics
Total: **180 Marks**
- Requirement Analysis: 30
- Test Case Design: 40
- Manual Testing: 30
- SQL & DB Testing: 25
- Automation Framework: 25
- UI/API Automation: 20
- Documentation: 10

---

## Submission Guidelines
- Compile documents → PDF
- Zip all files (<20MB)
- Upload via dashboard
