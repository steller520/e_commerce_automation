package com.e_commerce.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.e_commerce.utils.DbUtil;

import java.util.List;
import java.util.Map;

public class DbUtilH2Test {
    private DbUtil db;

    @BeforeMethod
    public void setUp() {
        db = new DbUtil(); // uses default db.properties (H2 sample)
    }

    @AfterMethod
    public void tearDown() {
        if (db != null) db.close();
    }

    @Test(description = "Validate DbUtil with H2 in-memory database")
    public void testDbUtilWithH2() throws Exception {
        db.execute("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100))");
        db.execute("INSERT INTO users (id, name) VALUES (?, ?)", 1, "Alice");
        db.execute("INSERT INTO users (id, name) VALUES (?, ?)", 2, "Bob");

        List<Map<String, Object>> rows = db.query("SELECT * FROM users ORDER BY id");
        Assert.assertEquals(rows.size(), 2, "Should have 2 users");
        Assert.assertEquals(rows.get(0).get("NAME"), "Alice");
        Assert.assertEquals(rows.get(1).get("NAME"), "Bob");

        Object count = db.querySingleValue("SELECT COUNT(*) FROM users").orElse(0);
        Assert.assertEquals(Integer.parseInt(count.toString()), 2);
    }
}
