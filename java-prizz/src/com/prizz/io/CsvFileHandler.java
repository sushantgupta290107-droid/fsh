package com.prizz.io;

import com.prizz.model.Booking;
import com.prizz.model.Request;
import com.prizz.model.RequestStatus;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Syllabus: Lectures 49-51 - Character streams: FileReader and FileWriter
// Syllabus: Lectures 52-54 - File Handling + Collections Integration: text/CSV read-write, validation, collection import/export, and exception-safe resource handling
public class CsvFileHandler {

    // Syllabus: Lectures 52-54 - Collection Export: Exporting List<Booking> to CSV via FileWriter
    public static void exportBookingsToCsv(String filePath, List<Booking> bookings) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        // Syllabus: Lectures 49-51 & 52-54 - FileWriter with try-with-resources
        try (FileWriter writer = new FileWriter(file)) {
            // Header
            writer.write("id,request_id,customer_id,provider_id,agreed_amount,status,booked_date\n");
            for (Booking b : bookings) {
                String line = String.format("%s,%s,%s,%s,%.2f,%s,%s\n",
                        b.getId(),
                        b.getRequestId(),
                        b.getCustomerId(),
                        b.getProviderId(),
                        b.getAgreedAmount(),
                        b.getStatus().name(),
                        b.getBookedDate());
                writer.write(line);
            }
        }
        System.out.println("[CSV Export] Successfully exported " + bookings.size() + " bookings to: " + filePath);
    }

    // Syllabus: Lectures 52-54 - Collection Import: Reading CSV with FileReader, validating lines, and populating List<Request>
    public static List<Request> importRequestsFromCsv(String filePath) throws IOException {
        List<Request> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("[CSV Import] File not found: " + filePath);
            return list;
        }

        // Syllabus: Lectures 49-51 - FileReader character stream
        StringBuilder raw = new StringBuilder();
        try (FileReader reader = new FileReader(file)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                raw.append((char) ch);
            }
        }

        String[] lines = raw.toString().split("\\r?\\n");
        boolean isHeader = true;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (isHeader) {
                isHeader = false;
                continue; // Skip CSV header
            }

            // Syllabus: Lectures 52-54 - Validation of CSV line
            String[] tokens = line.split(",");
            if (tokens.length < 7) {
                System.err.println("[CSV Validation Warning] Skipping invalid record (insufficient columns): " + line);
                continue;
            }

            String id = tokens[0].trim();
            String customerId = tokens[1].trim();
            String title = tokens[2].trim();
            String description = tokens[3].trim();
            String category = tokens[4].trim();
            String budgetStr = tokens[5].trim();
            String statusStr = tokens[6].trim();

            double budget;
            try {
                budget = Double.parseDouble(budgetStr);
            } catch (NumberFormatException e) {
                System.err.println("[CSV Validation Warning] Skipping record with invalid budget number: " + budgetStr);
                continue;
            }

            RequestStatus status = RequestStatus.OPEN;
            try {
                status = RequestStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}

            String createdAt = tokens.length > 7 ? tokens[7].trim() : "2026-11-24";
            Request req = new Request(id, customerId, title, description, category, budget, status, createdAt);

            // Syllabus: Lectures 52-54 - Adding validated entity to collection
            list.add(req);
        }

        System.out.println("[CSV Import] Successfully imported and validated " + list.size() + " requests from: " + filePath);
        return list;
    }
}
