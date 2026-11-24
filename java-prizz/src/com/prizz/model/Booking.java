package com.prizz.model;

// Syllabus: Core Java OOP - Booking transaction model
public class Booking {
    private String id;
    private String requestId;
    private String customerId;
    private String providerId;
    private double agreedAmount;
    private BookingStatus status;
    private String bookedDate;

    public Booking(String id, String requestId, String customerId, String providerId, double agreedAmount, BookingStatus status, String bookedDate) {
        this.id = id;
        this.requestId = requestId;
        this.customerId = customerId;
        this.providerId = providerId;
        this.agreedAmount = agreedAmount;
        this.status = status;
        this.bookedDate = bookedDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public double getAgreedAmount() { return agreedAmount; }
    public void setAgreedAmount(double agreedAmount) { this.agreedAmount = agreedAmount; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getBookedDate() { return bookedDate; }
    public void setBookedDate(String bookedDate) { this.bookedDate = bookedDate; }

    @Override
    public String toString() {
        return String.format("Booking #%s | Req: %s | Customer: %s | Provider: %s | Agreed: $%.2f | Status: %s",
                id, requestId, customerId, providerId, agreedAmount, status);
    }
}
