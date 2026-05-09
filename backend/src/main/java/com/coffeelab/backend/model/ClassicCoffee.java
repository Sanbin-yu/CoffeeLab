package com.coffeelab.backend.model;

import java.util.List;

public class ClassicCoffee {
    public Long id;
    public String name;
    public String imageUrl;
    public String description;
    public Integer caffeineLevel;
    public String suitableCrowd;
    public List<String> tags;
    public Recipe defaultRecipe;
    public Boolean adjustable;
}
