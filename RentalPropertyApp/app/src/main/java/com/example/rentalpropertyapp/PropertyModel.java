package com.example.rentalpropertyapp;

public class PropertyModel {
    private String title, description, location, username;
    private PropertyType type;
    private Double price;

    public enum PropertyType {
        APARTMENT,
        HOUSE,
        CONDO,
        STUDIO
    }

    // Full constructor
    public PropertyModel(String title, String description, String location, PropertyType type, Double price, String username) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.price = price;
        this.username = username;
    }

    // Constructor for backward compatibility
    public PropertyModel(String title, String description, String location, PropertyType type, String price) {
        this(title, description, location, type, price != null ? Double.parseDouble(price) : 0.0, null);
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public PropertyType getType() {
        return type;
    }

    public Double getPrice() {
        return price;
    }

    public String getUsername() {
        return username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "PropertyModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", username='" + username + '\'' +
                '}';
    }
}
