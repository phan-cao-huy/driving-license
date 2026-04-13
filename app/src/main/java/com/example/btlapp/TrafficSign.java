package com.example.btlapp;

public class TrafficSign {
    private String name;
    private String description;
    private int imageResId;
    private String category;

    public TrafficSign(String name, String description, int imageResId, String category) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
        this.category = category;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public String getCategory() { return category; }
}
