package com.example.entity;

import java.util.List;

public class Dish {

    private int id;
    private String name;
    private double sellingPrice;
    private List<Ingredient> ingredients;

    public Dish() {}

    public Dish(int id, String name, double sellingPrice) {
        this.id = id;
        this.name = name;
        this.sellingPrice = sellingPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
}
