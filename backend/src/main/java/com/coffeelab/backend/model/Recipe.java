package com.coffeelab.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Recipe {
    public Long id;
    public Long userId;
    public String name;
    public String note;
    public String cupType;
    public String temperatureType;
    public String coffeeBase;
    public Integer espressoShots;
    public String milkType;
    public String sweetness;
    public String iceLevel;
    public List<String> syrups = new ArrayList<>();
    public String foam;
    public List<String> toppings = new ArrayList<>();
    public List<String> flavorTags = new ArrayList<>();
    public FlavorRadar flavorRadar = FlavorRadar.balanced();
    public Boolean isPublic = false;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
