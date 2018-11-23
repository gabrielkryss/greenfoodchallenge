package com.ecoone.mindfulmealplanner.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Meal {
    public String mealName;
    public String mealType;
    public String restaurantName;
    public String mealDescription;
    public boolean isGreen;
    public boolean isPrivate;
    public List<String> tags;
    public int totalCo2eAmount;
    public HashMap<String, Object> foodList;

    // extra fields for database communication
    public String displayName;
    public String iconName;
    public String userUid;
    public String location;

    public Meal() {
        tags = new ArrayList<>();
        foodList = new HashMap<>();
    }

    public void clear() {
        mealName = null;
        mealType = null;
        restaurantName = null;
        mealDescription = null;
        tags = new ArrayList<>();
        foodList = new HashMap<>();
        totalCo2eAmount = 0;
        isGreen = false;
        isPrivate = true;
    }

}
