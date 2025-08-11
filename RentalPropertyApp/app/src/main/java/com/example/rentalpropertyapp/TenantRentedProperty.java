package com.example.rentalpropertyapp;

public class TenantRentedProperty {
    private int rentalId;
    private int propertyId;
    private String propertyTitle;
    private String address;
    private String rentalPrice;
    private String description;
    private int landlordId;
    private String landlordName;
    private String landlordEmail;
    private String landlordPhone;
    private String rentedAt;

    public TenantRentedProperty(int rentalId, int propertyId, String propertyTitle, String address,
                                String rentalPrice, String description, int landlordId, String landlordName,
                                String landlordEmail, String landlordPhone, String rentedAt) {
        this.rentalId = rentalId;
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.address = address;
        this.rentalPrice = rentalPrice;
        this.description = description;
        this.landlordId = landlordId;
        this.landlordName = landlordName;
        this.landlordEmail = landlordEmail;
        this.landlordPhone = landlordPhone;
        this.rentedAt = rentedAt;
    }

    // Getters
    public int getRentalId() {
        return rentalId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public String getAddress() {
        return address;
    }

    public String getRentalPrice() {
        return rentalPrice;
    }

    public String getDescription() {
        return description;
    }

    public int getLandlordId() {
        return landlordId;
    }

    public String getLandlordName() {
        return landlordName;
    }

    public String getLandlordEmail() {
        return landlordEmail;
    }

    public String getLandlordPhone() {
        return landlordPhone;
    }

    public String getRentedAt() {
        return rentedAt;
    }
}