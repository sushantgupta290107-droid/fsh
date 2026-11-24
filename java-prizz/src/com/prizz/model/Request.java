package com.prizz.model;

// Syllabus: Core Java OOP - Data Model representing a service problem
public class Request {
    private String id;
    private String customerId;
    private String title;
    private String description;
    private String category;
    private double budget;
    private RequestStatus status;
    private String createdAt;

    public Request(String id, String customerId, String title, String description, String category, double budget, RequestStatus status, String createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.budget = budget;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("[%s] #%s: %s | Cat: %s | Budget: $%.2f | Status: %s",
                createdAt, id, title, category, budget, status);
    }
}
