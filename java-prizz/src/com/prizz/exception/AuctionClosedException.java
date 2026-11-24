package com.prizz.exception;

// Syllabus: Core Java Exception Handling - Custom Checked Exception for Expired Auctions
public class AuctionClosedException extends PrizzException {
    public AuctionClosedException(String message) {
        super(message);
    }
}
