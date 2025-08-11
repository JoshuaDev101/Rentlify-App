package com.example.rentalpropertyapp;

public class NotificationItem {
    private int id;
    private String status;
    private String request;
    private int propertyId;
    private String message;
    private boolean readStatus;
    private String createdAt;

    public NotificationItem(int id, String status, String request, int propertyId, String message, boolean readStatus, String createdAt) {
        this.id = id;
        this.status = status;
        this.request = request;
        this.propertyId = propertyId;
        this.message = message;
        this.readStatus = readStatus;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getRequest() {
        return request;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}