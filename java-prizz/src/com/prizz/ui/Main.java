package com.prizz.ui;

import com.prizz.concurrency.AuctionTimerTask;
import com.prizz.concurrency.LiveAuctionRoom;
import com.prizz.concurrency.ProviderBiddingWorker;
import com.prizz.exception.AuctionClosedException;
import com.prizz.exception.InvalidBidException;
import com.prizz.exception.ResourceNotFoundException;
import com.prizz.generic.DataContainer;
import com.prizz.generic.RepositoryHelper;
import com.prizz.io.AuditTokenizer;
import com.prizz.io.ByteStreamBackup;
import com.prizz.io.CsvFileHandler;
import com.prizz.model.Bid;
import com.prizz.model.Booking;
import com.prizz.model.Customer;
import com.prizz.model.Provider;
import com.prizz.model.Request;
import com.prizz.model.RequestStatus;
import com.prizz.model.User;
import com.prizz.repository.BidDAO;
import com.prizz.repository.BookingDAO;
import com.prizz.repository.DBConnection;
import com.prizz.repository.RequestDAO;
import com.prizz.repository.UserDAO;
import com.prizz.service.AuctionManager;
import com.prizz.stream.AnalyticsReportService;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

// Syllabus: Pure Console-based interactive main application strictly following Java syllabus
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuctionManager auctionManager = new AuctionManager();
    private static final RequestDAO requestDAO = new RequestDAO();
    private static final BidDAO bidDAO = new BidDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();
    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {
        // Syllabus: Lectures 34-36 - Initialize database and create tables via JDBC
        DBConnection.initializeDatabase();
        seedInitialDataIfEmpty();

        boolean running = true;
        while (running) {
            ConsoleView.printHeader("PRIZZ - Reverse Bidding & Service Marketplace (Core Java)");
            System.out.println("  [1] Customer Menu (Post Request, View Bids, Accept Offer)");
            System.out.println("  [2] Provider Menu (Browse Open Requests, Submit Bid)");
            System.out.println("  [3] Concurrency Demo: Live Producer-Consumer & wait()/notify()");
            System.out.println("  [4] JDBC Database Operations & Direct CRUD Verification");
            System.out.println("  [5] Java Stream API & Lambdas (filter, map, sorted, limit, etc.)");
            System.out.println("  [6] Generics & Wildcards Demo (Generic Class & Method with bounds)");
            System.out.println("  [7] File I/O Demo (Byte Streams, Character Streams & StreamTokenizer)");
            System.out.println("  [8] Online Payment Gateway (Stripe/UPI)");
            System.out.println("  [9] Real-time Multi-node WebSockets Sync");
            System.out.println("  [10] AI-Powered Provider Matching Engine");
            System.out.println("  [0] Exit");
            ConsoleView.printDivider();
            System.out.print("Select an option (0-10): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    handleCustomerMenu();
                    break;
                case "2":
                    handleProviderMenu();
                    break;
                case "3":
                    runConcurrencyDemo();
                    break;
                case "4":
                    runJdbcDemo();
                    break;
                case "5":
                    runStreamApiDemo();
                    break;
                case "6":
                    runGenericsDemo();
                    break;
                case "7":
                    runFileIoDemo();
                    break;
                case "8":
                    // Syllabus Step 2: Out-of-syllabus feature placeholder
                    ConsoleView.printComingSoon("Online Payment Gateway (Stripe/UPI)");
                    break;
                case "9":
                    // Syllabus Step 2: Out-of-syllabus feature placeholder
                    ConsoleView.printComingSoon("Real-time Multi-node WebSockets Sync");
                    break;
                case "10":
                    // Syllabus Step 2: Out-of-syllabus feature placeholder
                    ConsoleView.printComingSoon("AI-Powered Provider Matching Engine");
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
        ConsoleView.printHeader("Customer Portal");
        System.out.println("  [1] Post a New Service Request");
        System.out.println("  [2] View All Requests");
        System.out.println("  [3] View Ranked Bids for a Request (PriorityQueue lowest-first)");
        System.out.println("  [4] Accept a Winning Bid & Create Booking");
        System.out.print("Select: ");
        String choice = scanner.nextLine().trim();

        try {
            if ("1".equals(choice)) {
                System.out.print("Enter Title (e.g. '150 Chairs for Event'): ");
                String title = scanner.nextLine().trim();
                System.out.print("Enter Description: ");
                String desc = scanner.nextLine().trim();
                System.out.print("Enter Category (e.g. Event Rentals, Plumbing, Electrical): ");
                String cat = scanner.nextLine().trim();
                System.out.print("Enter Maximum Budget ($): ");
                double budget = Double.parseDouble(scanner.nextLine().trim());

                String reqId = "REQ-" + (System.currentTimeMillis() % 10000);
                Request req = new Request(reqId, "CUST-001", title, desc, cat, budget, RequestStatus.OPEN, "2026-11-24");
                // Syllabus: Lectures 37-39 - PreparedStatement insert via DAO
                auctionManager.postNewRequest(req);
                System.out.println("[Success] Request created with ID: " + reqId);

            } else if ("2".equals(choice)) {
                List<Request> all = requestDAO.findAll();
                System.out.println("\n--- All Service Requests (" + all.size() + ") ---");
                for (Request r : all) {
                    System.out.println(r);
                }
            } else if ("3".equals(choice)) {
                System.out.print("Enter Request ID to inspect bids: ");
                String reqId = scanner.nextLine().trim();

                // Syllabus: Core Java Collections - PriorityQueue lowest bid ranking
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
            System.err.println("[Error] Invalid numerical input format!");
        } catch (Exception e) {
            System.err.println("[Error] Operation failed: " + e.getMessage());
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
                System.out.print("Enter Request ID to bid on: ");
                String reqId = scanner.nextLine().trim();
                System.out.print("Enter Your Provider ID (e.g. PROV-001): ");
                String provId = scanner.nextLine().trim();
                System.out.print("Enter Your Bid Amount ($): ");
                double amount = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Enter Estimated Hours: ");
                int hours = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter Proposal Notes: ");
                String notes = scanner.nextLine().trim();

                String bidId = "BID-" + (System.currentTimeMillis() % 10000);
                Bid bid = new Bid(bidId, reqId, provId, amount, hours, notes, "2026-11-24");

                // Syllabus: Core Java Custom Exception handling
                auctionManager.placeBid(bid);
                System.out.println("[Success] Bid #" + bidId + " placed successfully!");
            }
        } catch (InvalidBidException | AuctionClosedException | ResourceNotFoundException e) {
            System.err.println("[Custom Business Rule Violation] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("[Error] Invalid number input!");
        } catch (Exception e) {
            System.err.println("[Error] Could not submit bid: " + e.getMessage());
        }
    }

    // Syllabus: Lectures 32-33 - Live Producer-Consumer Concurrency Demo
    public static void runConcurrencyDemo() {
        ConsoleView.printHeader("Concurrency Demo: Producer-Consumer with wait() & notify()");
        System.out.println("Starting a live 4-second auction room (Room-DEMO-01)...");
        System.out.println("Spawning 3 Provider Producer threads + 1 Consumer thread...");

        LiveAuctionRoom room = new LiveAuctionRoom("DEMO-01");

        // Producer 1
        Thread p1 = new Thread(new ProviderBiddingWorker(room, "PROV-Alpha", 450.0, 4, "High quality white chairs"), "Worker-Alpha");
        // Producer 2
        Thread p2 = new Thread(new ProviderBiddingWorker(room, "PROV-Beta", 410.0, 3, "Same day delivery guaranteed"), "Worker-Beta");
        // Producer 3
        Thread p3 = new Thread(new ProviderBiddingWorker(room, "PROV-Gamma", 380.0, 2, "Lowest price match"), "Worker-Gamma");

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                while (!room.isClosed()) {
                    Bid consumed = room.consumeBid();
                    if (consumed != null) {
                        System.out.println("  >>> [AUDIT CONSUMER] Logged Bid #" + consumed.getId() + " from " + consumed.getProviderId());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Auction-Consumer");

        // Timer thread
        Thread timer = new Thread(new AuctionTimerTask(room, 3), "Auction-Timer");

        // Start all threads
        consumer.start();
        p1.start();
        p2.start();
        p3.start();
        timer.start();

        try {
            // Syllabus: Basic thread coordination using join()
            p1.join();
            p2.join();
            p3.join();
            timer.join();
            consumer.join(1000);
            System.out.println("[Concurrency Demo Finished] All threads completed safely with correct monitor synchronization.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Syllabus: Lectures 34-42 - Direct JDBC CRUD verification
    public static void runJdbcDemo() {
        ConsoleView.printHeader("JDBC & Database Programming Demo");
        try {
            System.out.println("1. Testing JDBC Connection...");
            System.out.println("   Connection Status: " + (DBConnection.getConnection() != null ? "ACTIVE (SQLite)" : "FAILED"));

            System.out.println("2. Testing PreparedStatement INSERT...");
            String testId = "REQ-JDBC-TEST";
            Request testReq = new Request(testId, "CUST-001", "JDBC Verification Task", "Testing parameter binding", "Plumbing", 120.0, RequestStatus.OPEN, "2026-11-24");
            requestDAO.create(testReq);
            System.out.println("   [PreparedStatement] Created row: " + testId);

            System.out.println("3. Testing ResultSet typed getters & Java-object mapping...");
            Request fetched = requestDAO.findById(testId);
            System.out.println("   [ResultSet Mapping] Fetched: " + fetched);

            System.out.println("4. Testing PreparedStatement UPDATE...");
            fetched.setBudget(150.0);
            requestDAO.update(fetched);
            Request updated = requestDAO.findById(testId);
            System.out.println("   [ResultSet Mapping] Updated Budget: $" + updated.getBudget());

            System.out.println("5. Testing PreparedStatement DELETE...");
            requestDAO.delete(testId);
            Request afterDelete = requestDAO.findById(testId);
            System.out.println("   [ResultSet Mapping] Verify Deleted: " + (afterDelete == null ? "CONFIRMED (null)" : "STILL PRESENT"));

        } catch (SQLException e) {
            System.err.println("[JDBC Error] " + e.getMessage());
        }
    }

    // Syllabus: Lectures 43-46 - Stream API & Lambda demo
    public static void runStreamApiDemo() {
        ConsoleView.printHeader("Java Stream API & Lambda Expressions Demo");
        try {
            List<Bid> allBids = bidDAO.findAll();
            List<Request> allRequests = requestDAO.findAll();

            if (allBids.isEmpty()) {
                System.out.println("No bids in database to analyze. Adding temporary bids for stream demonstration...");
                allBids.add(new Bid("B-DEMO-1", "REQ-01", "P-101", 350.0, 3, "Notes 1", "2026-11-24"));
                allBids.add(new Bid("B-DEMO-2", "REQ-01", "P-102", 280.0, 2, "Notes 2", "2026-11-24"));
                allBids.add(new Bid("B-DEMO-3", "REQ-01", "P-103", 420.0, 5, "Notes 3", "2026-11-24"));
                allBids.add(new Bid("B-DEMO-4", "REQ-01", "P-104", 190.0, 1, "Notes 4", "2026-11-24"));
            }

            // Pipeline 1: filter() -> sorted() -> limit()
            AnalyticsReportService.displayTopCheapestBids(allBids, 400.0, 2);

            // Pipeline 2: map() -> distinct() -> sorted()
            AnalyticsReportService.displayUniqueCategories(allRequests);

            // Pipeline 3: sorted() -> skip() -> limit() -> map()
            AnalyticsReportService.displayRunnerUpBids(allBids);

        } catch (SQLException e) {
            System.err.println("[Stream Demo Error] " + e.getMessage());
        }
    }

    // Syllabus: Lectures 47-48 - Generics & Wildcards demo
    public static void runGenericsDemo() {
        ConsoleView.printHeader("Generics & Type Safety Demo");

        // Generic class with diamond operator
        DataContainer<String> stringBox = new DataContainer<>("Session Token", "AUTH-99238-SECURE");
        DataContainer<Double> numberBox = new DataContainer<>("Platform Commission", 15.75);

        System.out.println("1. Generic Class DataContainer<T> with Diamond Operator (<>):");
        System.out.println("   " + stringBox);
        System.out.println("   " + numberBox);

        // Generic method with bounded type <T extends Number>
        System.out.println("\n2. Generic Method with Bounded Type (<T extends Number>):");
        double pct = RepositoryHelper.calculatePercentage(35.0, 140.0);
        System.out.printf("   calculatePercentage(35, 140) -> %.2f%%\n", pct);

        // Wildcard <? extends User>
        System.out.println("\n3. Wildcard Method (List<? extends User>):");
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("C-1", "Alice", "alice@example.com", "555-0101", "42 Elm St", "Event Rentals"));
        customers.add(new Customer("C-2", "Bob", "bob@example.com", "555-0102", "10 Oak Ave", "Plumbing"));
        RepositoryHelper.displayUsersSummary(customers);
    }

    // Syllabus: Lectures 49-54 - File I/O & CSV Collections integration
    public static void runFileIoDemo() {
        ConsoleView.printHeader("File I/O & CSV Collections Integration Demo");
        String csvExportPath = "data/exported_bookings.csv";
        String csvImportPath = "data/seed_requests.csv";
        String binaryBackupPath = "data/backup_state.bin";

        try {
            // 1. Character Stream FileWriter: Exporting Collection to CSV
            List<Booking> bookings = bookingDAO.findAll();
            if (bookings.isEmpty()) {
                bookings.add(new Booking("BK-SEED-1", "REQ-001", "CUST-001", "PROV-001", 420.0, com.prizz.model.BookingStatus.CONFIRMED, "2026-11-24"));
            }
            CsvFileHandler.exportBookingsToCsv(csvExportPath, bookings);

            // 2. Character Stream FileReader: Importing and Validating CSV into Collection
            List<Request> imported = CsvFileHandler.importRequestsFromCsv(csvImportPath);
            System.out.println("Sample imported request from CSV: " + (imported.isEmpty() ? "None" : imported.get(0)));

            // 3. Byte Streams: FileOutputStream & FileInputStream
            System.out.println("\n--- Byte Stream (FileInputStream / FileOutputStream) ---");
            ByteStreamBackup.createBinaryBackup(binaryBackupPath, "SYSTEM_BACKUP_TIMESTAMP=2026-11-24;STATUS=HEALTHY");
            String restored = ByteStreamBackup.readBinaryBackup(binaryBackupPath);
            System.out.println("Restored Content from binary file: " + restored);

            // 4. StreamTokenizer Analysis
            System.out.println("\n--- StreamTokenizer Analysis ---");
            AuditTokenizer.parseAndSummarizeAuditFile(csvExportPath);

        } catch (Exception e) {
            System.err.println("[File I/O Error] " + e.getMessage());
        }
    }

    private static void seedInitialDataIfEmpty() {
        try {
            // Ensure data directory exists
            new File("data").mkdirs();

            // Create sample seed_requests.csv if not exists
            File seedCsv = new File("data/seed_requests.csv");
            if (!seedCsv.exists()) {
                try (java.io.FileWriter fw = new java.io.FileWriter(seedCsv)) {
                    fw.write("id,customer_id,title,description,category,budget,status,created_at\n");
                    fw.write("REQ-001,CUST-001,150 Event Chairs,Folding chairs needed for garden reception,Event Rentals,500.0,OPEN,2026-11-24\n");
                    fw.write("REQ-002,CUST-002,Kitchen Sink Pipe Leak,Drain pipe leaking under sink,Plumbing,150.0,OPEN,2026-11-24\n");
                    fw.write("REQ-003,CUST-001,Living Room Painting,Two coats acrylic emulsion on 400sqft room,Painting,350.0,OPEN,2026-11-24\n");
                    fw.write("REQ-INVALID,CUST-003,Corrupted Row,Bad budget,Carpentry,INVALID_BUDGET,OPEN,2026-11-24\n");
                }
            }

            if (userDAO.findById("CUST-001") == null) {
                userDAO.create(new Customer("CUST-001", "Demo Customer", "customer@prizz.com", "555-0199", "Chandigarh Sector 34", "Event Rentals"));
            }
            if (userDAO.findById("PROV-001") == null) {
                userDAO.create(new Provider("PROV-001", "Fast Logistics & Rentals", "fast@rentals.com", "555-0800", "Event Rentals", 4.9, 28, 5600.0));
            }
            if (requestDAO.findById("REQ-001") == null) {
                requestDAO.create(new Request("REQ-001", "CUST-001", "150 Event Chairs", "Folding chairs needed for garden reception", "Event Rentals", 500.0, RequestStatus.OPEN, "2026-11-24"));
            }
            if (bidDAO.findById("BID-001") == null) {
                bidDAO.create(new Bid("BID-001", "REQ-001", "PROV-001", 420.0, 3, "150 white folding padded chairs", "2026-11-24"));
            }
        } catch (Exception ignored) {}
    }
}
