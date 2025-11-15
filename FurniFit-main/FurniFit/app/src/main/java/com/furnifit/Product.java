package com.furnifit;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String price;
    private String description;
    private float rating;
    private int reviewCount;
    private int imageResId;
    private String modelName; // New field for the GLB file name
    private String imagePath;

    public Product(String name, String price, String description, float rating, int reviewCount, int imageResId, String modelName, String imagePath) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.imageResId = imageResId;
        this.modelName = modelName;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getImagePath() {
        return imagePath;
    }
}
