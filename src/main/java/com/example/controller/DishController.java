package com.example.controller;

import com.example.entity.Dish;
import com.example.entity.Ingredient;
import com.example.repository.DishRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishRepository dishRepository;

    public DishController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    // GET /dishes
    @GetMapping
    public ResponseEntity<?> getAllDishes() {
        try {
            List<Dish> dishes = dishRepository.findAll();
            return ResponseEntity.ok(dishes);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // PUT /dishes/{id}/ingredients
    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<Map<String, Object>> ingredients
    ) {
        try {
            if (ingredients == null) {
                return ResponseEntity.status(400)
                        .body("Request body is required.");
            }
            if (!dishRepository.existsById(id)) {
                return ResponseEntity.status(404)
                        .body("Dish.id=" + id + " is not found");
            }
            dishRepository.updateIngredients(id, ingredients);
            return ResponseEntity.ok(dishRepository.findById(id));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/ingredients")
    public ResponseEntity<?> getDishIngredients(
            @PathVariable int id,
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) Double ingredientPriceAround
    ) {
        try {
            if (!dishRepository.existsById(id)) {
                return ResponseEntity.status(404)
                        .body("Dish.id=" + id + " is not found");
            }

            List<Ingredient> ingredients = dishRepository.findIngredientsByDishId(
                    id, ingredientName, ingredientPriceAround
            );

            return ResponseEntity.ok(ingredients);

        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}
