package com.example.repository;

import com.example.datasource.DataSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Repository
public class StockMovementRepository {

    private final DataSource dataSource;

    public StockMovementRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // GET /ingredients/{id}/stock?at={temporal}&unit={unit}
    public Map<String, Object> getStock(int ingredientId, String at, String unit) throws SQLException {
        String sql = """
                SELECT COALESCE(SUM(quantity), 0) AS stock
                FROM stock_movement
                WHERE ingredient_id = ?
                  AND unit = ?
                  AND moved_at <= ?::timestamp
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ps.setString(2, unit);
            ps.setString(3, at);

            ResultSet rs = ps.executeQuery();
            Map<String, Object> result = new HashMap<>();
            result.put("unit", unit);
            if (rs.next()) {
                result.put("stock", rs.getDouble("stock"));
            } else {
                result.put("stock", 0.0);
            }
            return result;
        }
    }
}
