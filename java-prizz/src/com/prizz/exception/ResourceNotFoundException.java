package com.prizz.exception;

// Syllabus: Core Java Exception Handling - Custom Checked Exception for Missing Entities
public class ResourceNotFoundException extends PrizzException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
