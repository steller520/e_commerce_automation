package com.e_commerce.tests;

import com.e_commerce.core.BaseTest;
import com.e_commerce.utils.DbUtil;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class DatabaseValidationTest extends BaseTest {
    private com.e_commerce.utils.DbUtil db;

    @BeforeMethod
    public void setUpDb() {
        db = new DbUtil();
    }

    @AfterMethod
    public void tearDownDb() {
        if (db != null) db.close();
    }

    @Test(description = "DB Test: Verify cart data integrity")
    public void validateCartDataIntegrity() throws Exception {
        // Create test cart scenario
        db.execute("CREATE TABLE IF NOT EXISTS carts (id INT PRIMARY KEY, user_id INT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        db.execute("CREATE TABLE IF NOT EXISTS cart_items (id INT PRIMARY KEY, cart_id INT, product_id INT, quantity INT, price DECIMAL(10,2))");

        // Insert test cart
        db.execute("INSERT INTO carts (id, user_id) VALUES (?, ?)", 1, 100);
        db.execute("INSERT INTO cart_items (id, cart_id, product_id, quantity, price) VALUES (?, ?, ?, ?, ?)", 1, 1, 501, 2, 29.99);

        // Validate cart exists
        List<Map<String, Object>> carts = db.query("SELECT * FROM carts WHERE id = ?", 1);
        Assert.assertEquals(carts.size(), 1, "Cart should exist");
        Assert.assertEquals(carts.get(0).get("USER_ID"), 100, "Cart should belong to user 100");

        // Validate cart items
        List<Map<String, Object>> items = db.query("SELECT * FROM cart_items WHERE cart_id = ?", 1);
        Assert.assertEquals(items.size(), 1, "Cart should have 1 item");
        Assert.assertEquals(items.get(0).get("QUANTITY"), 2, "Quantity should be 2");

        // Cleanup
        db.execute("DROP TABLE IF EXISTS cart_items");
        db.execute("DROP TABLE IF EXISTS carts");
    }

    @Test(description = "DB Test: Verify order and payment consistency")
    public void validateOrderPaymentConsistency() throws Exception {
        // Create test tables
        db.execute("CREATE TABLE IF NOT EXISTS orders (id INT PRIMARY KEY, user_id INT, total DECIMAL(10,2), status VARCHAR(50))");
        db.execute("CREATE TABLE IF NOT EXISTS payments (id INT PRIMARY KEY, order_id INT, amount DECIMAL(10,2), status VARCHAR(50))");

        // Scenario: Order with payment
        db.execute("INSERT INTO orders (id, user_id, total, status) VALUES (?, ?, ?, ?)", 1, 100, 59.98, "confirmed");
        db.execute("INSERT INTO payments (id, order_id, amount, status) VALUES (?, ?, ?, ?)", 1, 1, 59.98, "success");

        // Validate order-payment consistency
        List<Map<String, Object>> result = db.query(
            "SELECT o.id, o.total, p.amount FROM orders o JOIN payments p ON o.id = p.order_id WHERE o.id = ?", 1);

        Assert.assertEquals(result.size(), 1, "Order-payment join should return 1 record");
        Assert.assertEquals(result.get(0).get("TOTAL").toString(), result.get(0).get("AMOUNT").toString(),
            "Order total should match payment amount");

        // Cleanup
        db.execute("DROP TABLE IF EXISTS payments");
        db.execute("DROP TABLE IF EXISTS orders");
    }

    @Test(description = "DB Test: Detect orphan payments (payment without order)")
    public void detectOrphanPayments() throws Exception {
        db.execute("CREATE TABLE IF NOT EXISTS orders (id INT PRIMARY KEY, user_id INT, total DECIMAL(10,2))");
        db.execute("CREATE TABLE IF NOT EXISTS payments (id INT PRIMARY KEY, order_id INT, amount DECIMAL(10,2), status VARCHAR(50))");

        // Create orphan payment (no matching order)
        db.execute("INSERT INTO payments (id, order_id, amount, status) VALUES (?, ?, ?, ?)", 99, 9999, 100.00, "success");

        // Find orphan payments
        List<Map<String, Object>> orphans = db.query(
            "SELECT p.* FROM payments p LEFT JOIN orders o ON p.order_id = o.id WHERE o.id IS NULL");

        Assert.assertFalse(orphans.isEmpty(), "Should detect orphan payment");
        Assert.assertEquals(orphans.get(0).get("ID"), 99, "Orphan payment should be id 99");

        // Cleanup
        db.execute("DROP TABLE IF EXISTS payments");
        db.execute("DROP TABLE IF EXISTS orders");
    }

    @Test(description = "DB Test: Verify stock reduction after order")
    public void validateStockReduction() throws Exception {
        db.execute("CREATE TABLE IF NOT EXISTS products (id INT PRIMARY KEY, name VARCHAR(100), stock INT)");
        db.execute("CREATE TABLE IF NOT EXISTS order_items (id INT PRIMARY KEY, product_id INT, quantity INT)");

        // Initial stock
        db.execute("INSERT INTO products (id, name, stock) VALUES (?, ?, ?)", 1, "Product A", 100);

        // Order placed
        db.execute("INSERT INTO order_items (id, product_id, quantity) VALUES (?, ?, ?)", 1, 1, 5);

        // Simulate stock reduction
        db.execute("UPDATE products SET stock = stock - ? WHERE id = ?", 5, 1);

        // Verify stock
        Object newStock = db.querySingleValue("SELECT stock FROM products WHERE id = ?", 1).orElse(0);
        Assert.assertEquals(Integer.parseInt(newStock.toString()), 95, "Stock should be reduced to 95");

        // Cleanup
        db.execute("DROP TABLE IF EXISTS order_items");
        db.execute("DROP TABLE IF EXISTS products");
    }
}
