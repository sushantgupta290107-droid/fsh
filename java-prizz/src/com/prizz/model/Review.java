package com.prizz.model;

// Syllabus: Core Java OOP - Review Model
public class Review {
    private String id;
    private String bookingId;
    private double rating;
    private String comment;

    public Review(String id, String bookingId, double rating, String comment) {
        this.id = id;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return String.format("Review #%s for Booking #%s -> Rating: %.1f | \"%s\"", id, bookingId, rating, comment);
    }
}
