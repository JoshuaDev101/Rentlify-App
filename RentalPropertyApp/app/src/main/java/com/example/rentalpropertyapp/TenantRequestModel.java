package com.example.rentalpropertyapp;

public class TenantRequestModel {
    private String title;
    private String status;
    private String requestedDate;
    private String requestType;
    private String requestId;

    public TenantRequestModel(String title, String status, String requestedDate, String requestType, String requestId) {
        this.title = title;
        this.status = status;
        this.requestedDate = requestedDate;
        this.requestType = requestType;
        this.requestId = requestId;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequestId() {
        return requestId;
    }
}