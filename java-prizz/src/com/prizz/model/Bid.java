package com.prizz.model;

// Syllabus: Core Java Collections - Implements Comparable for natural ordering (lowest price first in reverse auction)
public class Bid implements Comparable<Bid> {
    private String id;
    private String requestId;
    private String providerId;
    private double amount;
    private int estimatedHours;
    private String proposalNotes;
    private String timestamp;

    public Bid(String id, String requestId, String providerId, double amount, int estimatedHours, String proposalNotes, String timestamp) {
        this.id = id;
        this.requestId = requestId;
        this.providerId = providerId;
        this.amount = amount;
        this.estimatedHours = estimatedHours;
        this.proposalNotes = proposalNotes;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(int estimatedHours) { this.estimatedHours = estimatedHours; }

    public String getProposalNotes() { return proposalNotes; }
    public void setProposalNotes(String proposalNotes) { this.proposalNotes = proposalNotes; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    // Reverse auction natural order: lower price ranks higher
    @Override
    public int compareTo(Bid other) {
        return Double.compare(this.amount, other.amount);
    }

    @Override
    public String toString() {
        return String.format("Bid #%s by Provider [%s] -> $%.2f (%d hrs) | Note: %s",
                id, providerId, amount, estimatedHours, proposalNotes);
    }
}
