package com.prizz.service;

import com.prizz.model.Booking;
import com.prizz.model.Request;

// Syllabus: Lectures 6-9 & 13-15 - String immutability, StringBuilder formatting, and Wrapper classes
public class InvoiceFormatter {

    public static String generateInvoice(Booking booking, Request request) {
        // Syllabus: Lectures 6-9 - StringBuilder for mutable, efficient string formatting
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================================\n");
        sb.append("                  PRIZZ OFFICIAL INVOICE                \n");
        sb.append("========================================================\n");
        sb.append(String.format("Booking ID     : %s\n", booking.getId()));
        sb.append(String.format("Date           : %s\n", booking.getBookedDate()));
        sb.append(String.format("Status         : %s\n", booking.getStatus()));
        sb.append("--------------------------------------------------------\n");
        sb.append(String.format("Service Request: %s (#%s)\n", request.getTitle(), request.getId()));
        sb.append(String.format("Category       : %s\n", request.getCategory()));
        sb.append(String.format("Customer ID    : %s\n", booking.getCustomerId()));
        sb.append(String.format("Provider ID    : %s\n", booking.getProviderId()));
        sb.append("--------------------------------------------------------\n");

        // Syllabus: Lectures 6-9 - Wrapper classes (Double)
        Double originalBudget = Double.valueOf(request.getBudget());
        Double winningPrice = Double.valueOf(booking.getAgreedAmount());
        Double savings = Double.valueOf(originalBudget.doubleValue() - winningPrice.doubleValue());

        sb.append(String.format("Original Budget: $%.2f\n", originalBudget));
        sb.append(String.format("Agreed Price   : $%.2f\n", winningPrice));
        sb.append(String.format("Customer Saved : $%.2f\n", savings));
        sb.append("========================================================\n");
        sb.append("      Thank you for choosing PRIZZ Reverse Auction!     \n");
        sb.append("========================================================\n");

        return sb.toString();
    }
}
