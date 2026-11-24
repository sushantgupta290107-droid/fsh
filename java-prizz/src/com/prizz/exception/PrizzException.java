package com.prizz.exception;

// Syllabus: Core Java Exception Handling - Base Checked Custom Exception
public class PrizzException extends Exception {
    public PrizzException(String message) {
        super(message);
    }

    public PrizzException(String message, Throwable cause) {
        super(message, cause);
    }
}
