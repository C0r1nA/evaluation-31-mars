package com.example.controller;

import com.example.entity.Ingredient;
import com.example.repository.IngredientRepository;
import com.example.repository.StockMovementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;
    private final StockMovementRepository stockMovementRepository;

    public IngredientController(IngredientRepository ingredientRepository,
                                StockMovementRepository stockMovementRepository) {
        this.ingredientRepository = ingredientRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    // GET /ingredients
    @GetMapping
    public ResponseEntity<?> getAllIngredients() {
        try {
            List<Ingredient> ingredients = ingredientRepository.findAll();
            return ResponseEntity.ok(ingredients);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // GET /ingredients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable int id) {
        try {
            Ingredient ingredient = ingredientRepository.findById(id);
            if (ingredient == null) {
                return ResponseEntity.status(404)
                        .body("Ingredient.id=" + id + " is not found");
            }
            return ResponseEntity.ok(ingredient);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // GET /ingredients/{id}/stock?at={temporal}&unit={unit}
    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getIngredientStock(
            @PathVariable int id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit
    ) {
        try {
            if (at == null || unit == null) {
                return ResponseEntity.status(400)
                        .body("Either mandatory query parameter `at` or `unit` is not provided.");
            }

            Ingredient ingredient = ingredientRepository.findById(id);
            if (ingredient == null) {
                return ResponseEntity.status(404)
                        .body("Ingredient.id=" + id + " is not found");
            }

            Map<String, Object> stock = stockMovementRepository.getStock(id, at, unit);
            return ResponseEntity.ok(stock);

        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}
