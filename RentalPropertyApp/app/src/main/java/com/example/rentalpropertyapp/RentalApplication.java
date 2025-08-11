package com.example.rentalpropertyapp;

public class RentalApplication {
    private int applicationId;
    private String tenantName;
    private String propertyTitle;
    private String contactNumber;
    private String status;
    private String createdAt;

    public RentalApplication(int applicationId, String tenantName, String propertyTitle,
                             String contactNumber, String status, String createdAt) {
        this.applicationId = applicationId;
        this.tenantName = tenantName;
        this.propertyTitle = propertyTitle;
        this.contactNumber = contactNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getApplicationId() { return applicationId; }
    public String getTenantName() { return tenantName; }
    public String getPropertyTitle() { return propertyTitle; }
    public String getContactNumber() { return contactNumber; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    // ✅ Add this method so you can update status
    public void setStatus(String status) {
        this.status = status;
    }
}
