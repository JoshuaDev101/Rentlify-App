package com.example.rentalpropertyapp;

public class RentedProperty {
    private int rentalId;
    private int propertyId;
    private int tenantId;
    private String propertyTitle;
    private String address;
    private String rentalPrice;
    private String tenantName;
    private String tenantEmail;
    private String tenantPhone;
    private String rentedAt;

    public RentedProperty(int rentalId, int propertyId, int tenantId, String propertyTitle, String address,
                          String rentalPrice, String tenantName, String tenantEmail, String tenantPhone, String rentedAt) {
        this.rentalId = rentalId;
        this.propertyId = propertyId;
        this.tenantId = tenantId;
        this.propertyTitle = propertyTitle;
        this.address = address;
        this.rentalPrice = rentalPrice;
        this.tenantName = tenantName;
        this.tenantEmail = tenantEmail;
        this.tenantPhone = tenantPhone;
        this.rentedAt = rentedAt;
    }

    // Getters
    public int getRentalId() {
        return rentalId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public int getTenantId() {
        return tenantId;
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

    public String getTenantName() {
        return tenantName;
    }

    public String getTenantEmail() {
        return tenantEmail;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public String getRentedAt() {
        return rentedAt;
    }
}