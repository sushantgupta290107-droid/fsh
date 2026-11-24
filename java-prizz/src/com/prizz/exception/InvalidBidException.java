package com.prizz.exception;

// Syllabus: Core Java Exception Handling - Custom Checked Exception for Invalid Bid Values
public class InvalidBidException extends PrizzException {
    public InvalidBidException(String message) {
        super(message);
    }
}
