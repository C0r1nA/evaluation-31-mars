package com.example.repository;

import com.example.datasource.DataSource;
import com.example.entity.Ingredient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // GET /ingredients
    public List<Ingredient> findAll() throws SQLException {
        String sql = "SELECT id, name, category, price FROM ingredient ORDER BY id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Ingredient> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapIngredient(rs));
            }
            return list;
        }
    }

    // GET /ingredients/{id}
    public Ingredient findById(int id) throws SQLException {
        String sql = "SELECT id, name, category, price FROM ingredient WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapIngredient(rs);
            }
            return null;
        }
    }

    private Ingredient mapIngredient(ResultSet rs) throws SQLException {
        return new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("price")
        );
    }
}
