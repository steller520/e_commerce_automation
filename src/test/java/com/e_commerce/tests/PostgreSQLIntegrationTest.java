package com.e_commerce.tests;

import com.e_commerce.utils.DbUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * PostgreSQL Integration Tests - Real Database Validation
 * Tests against actual ecommerce_checkout_db PostgreSQL database
 */
public class PostgreSQLIntegrationTest {
    private DbUtil db;

    @BeforeClass
    public void setUp() {
        db = new DbUtil(); // Uses PostgreSQL from db.properties
        System.out.println("✅ Connected to PostgreSQL: ecommerce_checkout_db");
    }

    @AfterClass
    public void tearDown() {
        if (db != null) {
            db.close();
            System.out.println("✅ PostgreSQL connection closed");
        }
    }

    @Test(priority = 1, description = "PostgreSQL: Verify database connection")
    public void testDatabaseConnection() throws Exception {
        // Verify we can connect and query PostgreSQL
        Object result = db.querySingleValue("SELECT 1").orElse(null);
        Assert.assertNotNull(result, "Database connection should work");
        Assert.assertEquals(result.toString(), "1", "Query should return 1");
        System.out.println("✅ PostgreSQL connection verified");
    }

    @Test(priority = 2, description = "PostgreSQL: Create and verify users table")
    public void testUsersTable() throws Exception {
        // Drop table if exists
        db.execute("DROP TABLE IF EXISTS users CASCADE");
        
        // Create users table
        db.execute("CREATE TABLE users (" +
            "id SERIAL PRIMARY KEY, " +
            "email VARCHAR(255) UNIQUE NOT NULL, " +
            "password VARCHAR(255) NOT NULL, " +
            "full_name VARCHAR(100), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // Insert test users
        db.execute("INSERT INTO users (email, password, full_name) VALUES (?, ?, ?)", 
            "test1@example.com", "pass123", "Test User 1");
        db.execute("INSERT INTO users (email, password, full_name) VALUES (?, ?, ?)", 
            "test2@example.com", "pass456", "Test User 2");

        // Query users
        List<Map<String, Object>> users = db.query("SELECT * FROM users ORDER BY id");
        Assert.assertEquals(users.size(), 2, "Should have 2 users");
        Assert.assertEquals(users.get(0).get("email"), "test1@example.com");
        Assert.assertEquals(users.get(1).get("full_name"), "Test User 2");

        System.out.println("✅ Users table created and validated");
    }

    @Test(priority = 3, description = "PostgreSQL: Create and verify products table")
    public void testProductsTable() throws Exception {
        db.execute("DROP TABLE IF EXISTS products CASCADE");
        
        db.execute("CREATE TABLE products (" +
            "id SERIAL PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "price DECIMAL(10,2) NOT NULL, " +
            "stock INT DEFAULT 0, " +
            "category VARCHAR(100), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        // Insert test products
        db.execute("INSERT INTO products (name, price, stock, category) VALUES (?, ?, ?, ?)", 
            "Blue Top", 500.00, 50, "Women");
        db.execute("INSERT INTO products (name, price, stock, category) VALUES (?, ?, ?, ?)", 
            "Men Tshirt", 400.00, 30, "Men");
        db.execute("INSERT INTO products (name, price, stock, category) VALUES (?, ?, ?, ?)", 
            "Sleeveless Dress", 1000.00, 10, "Women");

        // Verify products
        List<Map<String, Object>> products = db.query("SELECT * FROM products WHERE category = ?", "Women");
        Assert.assertEquals(products.size(), 2, "Should have 2 women's products");

        // Test stock query
        Object totalStock = db.querySingleValue("SELECT SUM(stock) FROM products").orElse(0);
        Assert.assertEquals(Integer.parseInt(totalStock.toString()), 90, "Total stock should be 90");

        System.out.println("✅ Products table created with 3 products");
    }

    @Test(priority = 4, description = "PostgreSQL: Create and verify carts and cart_items")
    public void testCartsAndCartItems() throws Exception {
        db.execute("DROP TABLE IF EXISTS cart_items CASCADE");
        db.execute("DROP TABLE IF EXISTS carts CASCADE");

        // Create carts table
        db.execute("CREATE TABLE carts (" +
            "id SERIAL PRIMARY KEY, " +
            "user_id INT NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES users(id))");

        // Create cart_items table
        db.execute("CREATE TABLE cart_items (" +
            "id SERIAL PRIMARY KEY, " +
            "cart_id INT NOT NULL, " +
            "product_id INT NOT NULL, " +
            "quantity INT NOT NULL, " +
            "price DECIMAL(10,2) NOT NULL, " +
            "FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (product_id) REFERENCES products(id))");

        // Get user and product IDs
        Object userId = db.querySingleValue("SELECT id FROM users LIMIT 1").orElse(1);
        Object productId = db.querySingleValue("SELECT id FROM products WHERE name = 'Blue Top'").orElse(1);

        // Create cart
        db.execute("INSERT INTO carts (user_id) VALUES (?)", userId);
        Object cartId = db.querySingleValue("SELECT id FROM carts WHERE user_id = ?", userId).orElse(1);

        // Add items to cart
        db.execute("INSERT INTO cart_items (cart_id, product_id, quantity, price) VALUES (?, ?, ?, ?)", 
            cartId, productId, 2, 500.00);

        // Verify cart with JOIN
        List<Map<String, Object>> cartDetails = db.query(
            "SELECT c.id as cart_id, u.email, ci.quantity, p.name, ci.price " +
            "FROM carts c " +
            "JOIN users u ON c.user_id = u.id " +
            "JOIN cart_items ci ON c.id = ci.cart_id " +
            "JOIN products p ON ci.product_id = p.id " +
            "WHERE c.id = ?", cartId);

        Assert.assertFalse(cartDetails.isEmpty(), "Cart should have items");
        Assert.assertEquals(cartDetails.get(0).get("quantity"), 2);
        Assert.assertEquals(cartDetails.get(0).get("name"), "Blue Top");

        System.out.println("✅ Cart and cart_items tables created with foreign keys");
    }

    @Test(priority = 5, description = "PostgreSQL: Create and verify orders and payments")
    public void testOrdersAndPayments() throws Exception {
        db.execute("DROP TABLE IF EXISTS payments CASCADE");
        db.execute("DROP TABLE IF EXISTS order_items CASCADE");
        db.execute("DROP TABLE IF EXISTS orders CASCADE");

        // Create orders table
        db.execute("CREATE TABLE orders (" +
            "id SERIAL PRIMARY KEY, " +
            "user_id INT NOT NULL, " +
            "total_amount DECIMAL(10,2) NOT NULL, " +
            "status VARCHAR(50) DEFAULT 'pending', " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES users(id))");

        // Create order_items table
        db.execute("CREATE TABLE order_items (" +
            "id SERIAL PRIMARY KEY, " +
            "order_id INT NOT NULL, " +
            "product_id INT NOT NULL, " +
            "quantity INT NOT NULL, " +
            "price DECIMAL(10,2) NOT NULL, " +
            "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (product_id) REFERENCES products(id))");

        // Create payments table
        db.execute("CREATE TABLE payments (" +
            "id SERIAL PRIMARY KEY, " +
            "order_id INT NOT NULL UNIQUE, " +
            "amount DECIMAL(10,2) NOT NULL, " +
            "payment_method VARCHAR(50), " +
            "status VARCHAR(50) DEFAULT 'pending', " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (order_id) REFERENCES orders(id))");

        // Get user and product IDs
        Object userId = db.querySingleValue("SELECT id FROM users LIMIT 1").orElse(1);
        Object productId = db.querySingleValue("SELECT id FROM products WHERE name = 'Blue Top'").orElse(1);

        // Create order
        db.execute("INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)", 
            userId, 1000.00, "confirmed");
        Object orderId = db.querySingleValue("SELECT id FROM orders WHERE user_id = ? ORDER BY id DESC LIMIT 1", userId).orElse(1);

        // Add order items
        db.execute("INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)", 
            orderId, productId, 2, 500.00);

        // Create payment
        db.execute("INSERT INTO payments (order_id, amount, payment_method, status) VALUES (?, ?, ?, ?)", 
            orderId, 1000.00, "Credit Card", "success");

        // Verify order-payment consistency (C6 Issue)
        List<Map<String, Object>> orderPayments = db.query(
            "SELECT o.id, o.total_amount, p.amount, p.status " +
            "FROM orders o " +
            "JOIN payments p ON o.id = p.order_id " +
            "WHERE o.id = ?", orderId);

        Assert.assertFalse(orderPayments.isEmpty(), "Order should have payment");
        Assert.assertEquals(orderPayments.get(0).get("total_amount").toString(), 
                          orderPayments.get(0).get("amount").toString(), 
                          "Order total should match payment amount");

        System.out.println("✅ Orders and payments tables created - consistency verified");
    }

    @Test(priority = 6, description = "PostgreSQL: Detect orphan payments (C6 Issue)")
    public void detectOrphanPayments() throws Exception {
        // First, disable foreign key constraint temporarily
        db.execute("ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_order_id_fkey");
        
        // Create orphan payment (payment without valid order)
        db.execute("INSERT INTO payments (order_id, amount, payment_method, status) " +
            "VALUES (9999, 500.00, 'PayPal', 'success')");

        // Find orphan payments using LEFT JOIN
        List<Map<String, Object>> orphans = db.query(
            "SELECT p.id, p.order_id, p.amount, p.status " +
            "FROM payments p " +
            "LEFT JOIN orders o ON p.order_id = o.id " +
            "WHERE o.id IS NULL");

        Assert.assertFalse(orphans.isEmpty(), "Should detect orphan payment");
        Assert.assertEquals(orphans.get(0).get("order_id"), 9999);

        System.out.println("⚠️ FOUND ORPHAN PAYMENT: ID=" + orphans.get(0).get("id") + ", Amount=" + orphans.get(0).get("amount"));

        // Cleanup orphan
        db.execute("DELETE FROM payments WHERE order_id = 9999");
        
        // Re-enable foreign key constraint
        db.execute("ALTER TABLE payments ADD CONSTRAINT payments_order_id_fkey " +
            "FOREIGN KEY (order_id) REFERENCES orders(id)");
    }

    @Test(priority = 7, description = "PostgreSQL: Verify stock reduction after order (C6 Issue)")
    public void testStockReduction() throws Exception {
        // Get current stock
        Object initialStock = db.querySingleValue("SELECT stock FROM products WHERE name = 'Blue Top'").orElse(0);
        int currentStock = Integer.parseInt(initialStock.toString());

        // Simulate order placement - reduce stock
        int quantityOrdered = 5;
        db.execute("UPDATE products SET stock = stock - ? WHERE name = 'Blue Top'", quantityOrdered);

        // Verify stock reduced
        Object newStock = db.querySingleValue("SELECT stock FROM products WHERE name = 'Blue Top'").orElse(0);
        int updatedStock = Integer.parseInt(newStock.toString());

        Assert.assertEquals(updatedStock, currentStock - quantityOrdered, 
            "Stock should be reduced by order quantity");

        System.out.println("✅ Stock reduced: " + currentStock + " → " + updatedStock);

        // Restore stock
        db.execute("UPDATE products SET stock = stock + ? WHERE name = 'Blue Top'", quantityOrdered);
    }

    @Test(priority = 8, description = "PostgreSQL: Test coupons table")
    public void testCouponsTable() throws Exception {
        db.execute("DROP TABLE IF EXISTS coupon_usage CASCADE");
        db.execute("DROP TABLE IF EXISTS coupons CASCADE");

        db.execute("CREATE TABLE coupons (" +
            "id SERIAL PRIMARY KEY, " +
            "code VARCHAR(50) UNIQUE NOT NULL, " +
            "discount_percent INT, " +
            "valid_from DATE, " +
            "valid_until DATE, " +
            "max_uses INT DEFAULT 1, " +
            "current_uses INT DEFAULT 0)");

        db.execute("CREATE TABLE coupon_usage (" +
            "id SERIAL PRIMARY KEY, " +
            "coupon_id INT NOT NULL, " +
            "user_id INT NOT NULL, " +
            "order_id INT NOT NULL, " +
            "used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (coupon_id) REFERENCES coupons(id), " +
            "FOREIGN KEY (user_id) REFERENCES users(id), " +
            "FOREIGN KEY (order_id) REFERENCES orders(id))");

        // Insert test coupons (cast strings to DATE)
        db.execute("INSERT INTO coupons (code, discount_percent, valid_from, valid_until, max_uses) " +
            "VALUES (?, ?, ?::date, ?::date, ?)", "SAVE10", 10, "2026-01-01", "2026-12-31", 100);
        db.execute("INSERT INTO coupons (code, discount_percent, valid_from, valid_until, max_uses) " +
            "VALUES (?, ?, ?::date, ?::date, ?)", "EXPIRED2023", 20, "2023-01-01", "2023-12-31", 50);

        // Test valid coupon
        List<Map<String, Object>> validCoupons = db.query(
            "SELECT * FROM coupons WHERE code = ? AND CURRENT_DATE BETWEEN valid_from AND valid_until", "SAVE10");
        Assert.assertFalse(validCoupons.isEmpty(), "SAVE10 should be valid");

        // Test expired coupon
        List<Map<String, Object>> expiredCoupons = db.query(
            "SELECT * FROM coupons WHERE code = ? AND CURRENT_DATE BETWEEN valid_from AND valid_until", "EXPIRED2023");
        Assert.assertTrue(expiredCoupons.isEmpty(), "EXPIRED2023 should be expired");

        System.out.println("✅ Coupons table created - valid/expired logic verified");
    }

    @Test(priority = 9, description = "PostgreSQL: Complex JOIN query - Full checkout data")
    public void testComplexJoinQuery() throws Exception {
        // Complex query joining all tables
        List<Map<String, Object>> checkoutData = db.query(
            "SELECT " +
            "   u.email, " +
            "   o.id as order_id, " +
            "   o.total_amount, " +
            "   o.status as order_status, " +
            "   p.amount as payment_amount, " +
            "   p.payment_method, " +
            "   p.status as payment_status, " +
            "   oi.quantity, " +
            "   pr.name as product_name, " +
            "   oi.price " +
            "FROM users u " +
            "JOIN orders o ON u.id = o.user_id " +
            "JOIN payments p ON o.id = p.order_id " +
            "JOIN order_items oi ON o.id = oi.order_id " +
            "JOIN products pr ON oi.product_id = pr.id " +
            "WHERE p.status = 'success' " +
            "ORDER BY o.id DESC " +
            "LIMIT 5");

        Assert.assertFalse(checkoutData.isEmpty(), "Should have successful checkout records");
        
        for (Map<String, Object> row : checkoutData) {
            System.out.println("✅ Order: " + row.get("order_id") + 
                " | User: " + row.get("email") + 
                " | Product: " + row.get("product_name") + 
                " | Total: Rs. " + row.get("total_amount") + 
                " | Payment: " + row.get("payment_status"));
        }
    }

    @Test(priority = 10, description = "PostgreSQL: Verify referential integrity constraints")
    public void testReferentialIntegrity() throws Exception {
        // Try to insert cart_item with invalid cart_id (should fail)
        try {
            db.execute("INSERT INTO cart_items (cart_id, product_id, quantity, price) " +
                "VALUES (99999, 1, 1, 100.00)");
            Assert.fail("Should throw foreign key constraint violation");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("foreign key") || 
                            e.getMessage().contains("violates"), 
                "Should be foreign key constraint error");
            System.out.println("✅ Foreign key constraint enforced correctly");
        }
    }
}
