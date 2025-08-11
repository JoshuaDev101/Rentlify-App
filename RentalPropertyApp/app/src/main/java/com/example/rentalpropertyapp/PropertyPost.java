package com.example.rentalpropertyapp;

public class PropertyPost {
    private String propertyId;
    private String landlordId;
    private String title;
    private String description;
    private String imageUrl;
    private long timestamp;

    public PropertyPost() {
    } // Needed for Firebase

    public PropertyPost(String propertyId, String landlordId, String title, String description, String imageUrl, long timestamp) {
        this.propertyId = propertyId;
        this.landlordId = landlordId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Getters and setters...
}
