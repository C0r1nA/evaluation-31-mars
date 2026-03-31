package com.example.repository;

import com.example.datasource.DataSource;
import com.example.entity.Dish;
import com.example.entity.Ingredient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class DishRepository {

    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean existsById(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dish WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public List<Dish> findAll() throws SQLException {
        String sql = """
                SELECT d.id        AS d_id,
                       d.name      AS d_name,
                       d.selling_price,
                       i.id        AS i_id,
                       i.name      AS i_name,
                       i.category,
                       i.price
                FROM dish d
                LEFT JOIN dish_ingredient di ON di.dish_id = d.id
                LEFT JOIN ingredient i ON i.id = di.ingredient_id
                ORDER BY d.id
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Map<Integer, Dish> dishMap = new LinkedHashMap<>();
            while (rs.next()) {
                int dishId = rs.getInt("d_id");
                Dish dish = dishMap.computeIfAbsent(dishId, k -> {
                    try {
                        Dish d = new Dish(dishId, rs.getString("d_name"), rs.getDouble("selling_price"));
                        d.setIngredients(new ArrayList<>());
                        return d;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                int iId = rs.getInt("i_id");
                if (iId != 0) {
                    dish.getIngredients().add(new Ingredient(
                            iId,
                            rs.getString("i_name"),
                            rs.getString("category"),
                            rs.getDouble("price")
                    ));
                }
            }
            return new ArrayList<>(dishMap.values());
        }
    }

    public Dish findById(int id) throws SQLException {
        String sql = """
                SELECT d.id        AS d_id,
                       d.name      AS d_name,
                       d.selling_price,
                       i.id        AS i_id,
                       i.name      AS i_name,
                       i.category,
                       i.price
                FROM dish d
                LEFT JOIN dish_ingredient di ON di.dish_id = d.id
                LEFT JOIN ingredient i ON i.id = di.ingredient_id
                WHERE d.id = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            Dish dish = null;
            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish(id, rs.getString("d_name"), rs.getDouble("selling_price"));
                    dish.setIngredients(new ArrayList<>());
                }
                int iId = rs.getInt("i_id");
                if (iId != 0) {
                    dish.getIngredients().add(new Ingredient(
                            iId,
                            rs.getString("i_name"),
                            rs.getString("category"),
                            rs.getDouble("price")
                    ));
                }
            }
            return dish;
        }
    }

    public void updateIngredients(int dishId, List<Map<String, Object>> ingredients) throws SQLException {
        String deleteSql = "DELETE FROM dish_ingredient WHERE dish_id = ?";
        String insertSql = """
                INSERT INTO dish_ingredient (dish_id, ingredient_id)
                SELECT ?, id FROM ingredient WHERE id = ?
                """;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                    del.setInt(1, dishId);
                    del.executeUpdate();
                }
                try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    for (Map<String, Object> ing : ingredients) {
                        Object idObj = ing.get("id");
                        if (idObj != null) {
                            ins.setInt(1, dishId);
                            ins.setInt(2, Integer.parseInt(idObj.toString()));
                            ins.addBatch();
                        }
                    }
                    ins.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Ingredient> findIngredientsByDishId(
            int dishId,
            String ingredientName,
            Double ingredientPriceAround
    ) throws SQLException {

        StringBuilder sql = new StringBuilder("""
                SELECT i.id, i.name, i.category, i.price
                FROM ingredient i
                JOIN dish_ingredient di ON di.ingredient_id = i.id
                WHERE di.dish_id = ?
                """);

        if (ingredientName != null) {
            sql.append(" AND i.name ILIKE ?");
        }
        if (ingredientPriceAround != null) {
            sql.append(" AND i.price BETWEEN ? AND ?");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            ps.setInt(index++, dishId);

            if (ingredientName != null) {
                ps.setString(index++, "%" + ingredientName + "%");
            }
            if (ingredientPriceAround != null) {
                ps.setDouble(index++, ingredientPriceAround - 50);
                ps.setDouble(index++, ingredientPriceAround + 50);
            }

            ResultSet rs = ps.executeQuery();
            List<Ingredient> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price")
                ));
            }
            return list;
        }
    }
}
