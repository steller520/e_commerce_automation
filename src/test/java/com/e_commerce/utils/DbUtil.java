package com.e_commerce.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class DbUtil implements AutoCloseable {
    private final Properties props = new Properties();
    private HikariDataSource dataSource;

    public DbUtil() {
        this("db.properties");
    }

    public DbUtil(String propertiesResource) {
        loadProps(propertiesResource);
        initDataSource();
    }

    private void loadProps(String resource) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (is == null) {
                throw new IllegalStateException("Database properties resource not found: " + resource);
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DB properties", e);
        }
    }

    private void initDataSource() {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username", "");
        String pass = props.getProperty("db.password", "");
        String driver = props.getProperty("db.driverClass", "");
        int poolSize = Integer.parseInt(props.getProperty("db.pool.size", "5"));

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        if (!user.isEmpty()) config.setUsername(user);
        if (!pass.isEmpty()) config.setPassword(pass);
        if (!driver.isEmpty()) config.setDriverClassName(driver);
        config.setMaximumPoolSize(poolSize);
        config.setPoolName("DbUtilPool");

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public int execute(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            return ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return toList(rs);
            }
        }
    }

    public Optional<Object> querySingleValue(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getObject(1));
                }
                return Optional.empty();
            }
        }
    }

    private void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    private List<Map<String, Object>> toList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= colCount; i++) {
                String label = meta.getColumnLabel(i);
                row.put(label, rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
