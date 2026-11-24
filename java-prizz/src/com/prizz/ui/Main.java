package com.prizz.ui;

import com.prizz.exception.AuctionClosedException;
import com.prizz.exception.InvalidBidException;
import com.prizz.exception.ResourceNotFoundException;
import com.prizz.model.Admin;
import com.prizz.model.Bid;
import com.prizz.model.Booking;
import com.prizz.model.Customer;
import com.prizz.model.Provider;
import com.prizz.model.Request;
import com.prizz.model.RequestStatus;
import com.prizz.model.User;
import com.prizz.service.AuctionManager;
import com.prizz.service.InvoiceFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

// Syllabus: Pure Console Application - Continuous Evaluation 1 Focus (Lectures 1-15 & Core Collections)
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuctionManager auctionManager = new AuctionManager();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            ConsoleView.printHeader("PRIZZ - Reverse Bidding System (Continuous Evaluation 1)");
            System.out.println("  [1] Customer Portal (Create Request, View Bids, Accept Offer)");
            System.out.println("  [2] Service Provider Portal (Browse Requests, Submit Bids)");
            System.out.println("  [3] Invoicing & Savings Summary (StringBuilder & Wrappers)");
            System.out.println("  [4] Exception Handling Demo (Invalid Bids & Closed Auctions)");
            System.out.println("  [5] OOP Architecture Showcase (Inheritance & Polymorphism)");
            System.out.println("  [6] Evaluation 2 Modules Status (JDBC, Concurrency, Streams, File I/O)");
            System.out.println("  [0] Exit");
            ConsoleView.printDivider();
            System.out.print("Select an option (0-6): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    handleCustomerMenu();
                    break;
                case "2":
                    handleProviderMenu();
                    break;
                case "3":
                    handleInvoiceMenu();
                    break;
                case "4":
                    handleExceptionDemo();
                    break;
                case "5":
                    handleOopDemo();
                    break;
                case "6":
                    handleEvaluation2Status();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nExiting PRIZZ Console Application. Goodbye!");
                    break;
                default:
                    System.out.println("[Error] Invalid choice, please try again.");
            }
        }
    }

    private static void handleCustomerMenu() {
        ConsoleView.printHeader("Customer Portal (Lectures 1-15 & Collections)");
        System.out.println("  [1] Post a New Service Request");
        System.out.println("  [2] View All Service Requests");
        System.out.println("  [3] View Ranked Bids for Request (PriorityQueue lowest-price first)");
        System.out.println("  [4] Accept Winning Bid & Confirm Booking");
        System.out.print("Select: ");
        String choice = scanner.nextLine().trim();

        try {
            if ("1".equals(choice)) {
                System.out.print("Enter Problem Title (e.g., '150 Chairs for Event'): ");
                String title = scanner.nextLine().trim();
                System.out.print("Enter Description: ");
                String desc = scanner.nextLine().trim();
                System.out.print("Enter Category (e.g. Event Rentals, Plumbing, Painting): ");
                String cat = scanner.nextLine().trim();
                System.out.print("Enter Maximum Budget ($): ");
                double budget = Double.parseDouble(scanner.nextLine().trim());

                if (budget <= 0) {
                    System.err.println("[Validation Error] Budget must be greater than $0!");
                    return;
                }

                String reqId = "REQ-" + (System.currentTimeMillis() % 10000);
                Request req = new Request(reqId, "CUST-001", title, desc, cat, budget, RequestStatus.OPEN, "2026-11-24");
                auctionManager.postNewRequest(req);
                System.out.println("[Success] Request created with ID: " + reqId);

            } else if ("2".equals(choice)) {
                List<Request> all = auctionManager.getAllRequests();
                System.out.println("\n--- All Service Requests (" + all.size() + ") ---");
                for (Request r : all) {
                    System.out.println(r);
                }
            } else if ("3".equals(choice)) {
                System.out.print("Enter Request ID to inspect bids (e.g. REQ-001): ");
                String reqId = scanner.nextLine().trim();

                PriorityQueue<Bid> rankedBids = auctionManager.getRankedBidsForRequest(reqId);
                if (rankedBids.isEmpty()) {
                    System.out.println("[Info] No bids placed yet for Request #" + reqId);
                } else {
                    System.out.println("\n--- Ranked Bids for Request #" + reqId + " (Lowest Price First) ---");
                    int rank = 1;
                    while (!rankedBids.isEmpty()) {
                        Bid b = rankedBids.poll();
                        System.out.println(" Rank #" + (rank++) + " -> " + b);
                    }
                }
            } else if ("4".equals(choice)) {
                System.out.print("Enter Request ID: ");
                String reqId = scanner.nextLine().trim();
                System.out.print("Enter Winning Bid ID to Accept: ");
                String bidId = scanner.nextLine().trim();

                Booking booking = auctionManager.acceptWinningBid(reqId, bidId);
                System.out.println("[Success] Winning bid accepted! Created Booking:\n" + booking);
            }
        } catch (NumberFormatException e) {
            System.err.println("[Error] Invalid numeric format entered!");
        } catch (ResourceNotFoundException e) {
            System.err.println("[Resource Error] " + e.getMessage());
        }
    }

    private static void handleProviderMenu() {
        ConsoleView.printHeader("Service Provider Portal");
        System.out.println("  [1] Browse Active Bidding Requests");
        System.out.println("  [2] Submit a Competitive Bid");
        System.out.print("Select: ");
        String choice = scanner.nextLine().trim();

        try {
            if ("1".equals(choice)) {
                List<Request> active = auctionManager.getActiveRequests();
                System.out.println("\n--- Active Bidding Requests (" + active.size() + ") ---");
                for (Request r : active) {
                    System.out.println(r);
                }
            } else if ("2".equals(choice)) {
                System.out.print("Enter Request ID to bid on (e.g. REQ-001): ");
                String reqId = scanner.nextLine().trim();
                System.out.print("Enter Your Provider Name / ID: ");
                String provId = scanner.nextLine().trim();
                System.out.print("Enter Your Bid Amount ($): ");
                double amount = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Enter Estimated Completion Hours: ");
                int hours = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter Proposal Notes: ");
                String notes = scanner.nextLine().trim();

                String bidId = "BID-" + (System.currentTimeMillis() % 10000);
                Bid bid = new Bid(bidId, reqId, provId, amount, hours, notes, "2026-11-24");

                // Syllabus: Lectures 10-12 - Handling custom exceptions
                auctionManager.placeBid(bid);
                System.out.println("[Success] Bid #" + bidId + " placed successfully!");
            }
        } catch (InvalidBidException | AuctionClosedException | ResourceNotFoundException e) {
            System.err.println("[Custom Business Rule Violation] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("[Error] Invalid numerical input!");
        }
    }

    private static void handleInvoiceMenu() {
        ConsoleView.printHeader("Invoicing & StringBuilder Formatter (Lectures 6-9)");
        List<Booking> bookings = auctionManager.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("[Info] No confirmed bookings yet. Creating a sample confirmed booking...");
            try {
                Booking sample = auctionManager.acceptWinningBid("REQ-001", "BID-003");
                Request req = auctionManager.findRequestById("REQ-001");
                System.out.println(InvoiceFormatter.generateInvoice(sample, req));
            } catch (Exception e) {
                System.err.println("[Error] " + e.getMessage());
            }
        } else {
            Booking b = bookings.get(0);
            Request r = auctionManager.findRequestById(b.getRequestId());
            System.out.println(InvoiceFormatter.generateInvoice(b, r));
        }
    }

    private static void handleExceptionDemo() {
        ConsoleView.printHeader("Custom Exception Handling Demo (Lectures 10-12)");
        System.out.println("Testing defensive custom exceptions with try-catch:");

        // Test 1: Bid exceeding customer maximum budget
        try {
            System.out.println("\n1. Testing InvalidBidException (Bid $600.00 exceeds budget $500.00):");
            Bid badBid = new Bid("TEST-BID-1", "REQ-001", "PROV-TEST", 600.0, 2, "Too high", "2026-11-24");
            auctionManager.placeBid(badBid);
        } catch (InvalidBidException e) {
            System.out.println("   [CAUGHT EXPECTED EXCEPTION] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("   [UNEXPECTED] " + e);
        }

        // Test 2: Negative bid amount
        try {
            System.out.println("\n2. Testing InvalidBidException (Negative amount -$50.00):");
            Bid negBid = new Bid("TEST-BID-2", "REQ-001", "PROV-TEST", -50.0, 1, "Negative", "2026-11-24");
            auctionManager.placeBid(negBid);
        } catch (InvalidBidException e) {
            System.out.println("   [CAUGHT EXPECTED EXCEPTION] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("   [UNEXPECTED] " + e);
        }

        // Test 3: Request Not Found
        try {
            System.out.println("\n3. Testing ResourceNotFoundException (Non-existent request):");
            Bid fakeBid = new Bid("TEST-BID-3", "REQ-DOES-NOT-EXIST", "PROV-TEST", 100.0, 1, "Notes", "2026-11-24");
            auctionManager.placeBid(fakeBid);
        } catch (ResourceNotFoundException e) {
            System.out.println("   [CAUGHT EXPECTED EXCEPTION] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("   [UNEXPECTED] " + e);
        }
    }

    private static void handleOopDemo() {
        ConsoleView.printHeader("OOP Inheritance & Polymorphism Demo (Lectures 1-5)");
        List<User> userList = new ArrayList<>();
        userList.add(new Customer("C-101", "Aarav Sharma", "aarav@example.com", "9876543210", "Sector 17, Chandigarh", "Event Rentals"));
        userList.add(new Provider("P-202", "Priya Verma", "priya@repairs.com", "9812345678", "Plumbing", 4.8, 42, 8400.0));
        userList.add(new Admin("A-303", "Admin User", "admin@prizz.com", "9900000000", 3));

        System.out.println("Polymorphic call to abstract method displayProfile():\n");
        for (User u : userList) {
            // Syllabus: Lectures 1-5 - Polymorphism (dynamic method dispatch)
            u.displayProfile();
            System.out.println();
        }
    }

    private static void handleEvaluation2Status() {
        ConsoleView.printHeader("Evaluation 2 Syllabus Modules (Lectures 32-54)");
        System.out.println("As per your syllabus schedule, the following modules are designed for Second Evaluation:");
        System.out.println("  • Lectures 32-33: Inter-thread communication (wait(), notify(), guarded blocks)");
        System.out.println("  • Lectures 34-42: JDBC & Database Programming (PreparedStatement, ResultSet CRUD)");
        System.out.println("  • Lectures 43-46: Lambda Expressions & Stream API Pipelines");
        System.out.println("  • Lectures 47-48: Introductory Generics & Wildcards");
        System.out.println("  • Lectures 49-54: Byte Streams, Character Streams & StreamTokenizer");
        System.out.println("\n[STATUS]: Code is fully implemented in backend packages and ready for Second Evaluation.");
        System.out.println("To execute the Second Evaluation automated test suite, run: verify.bat");
    }
}
