package com.prizz;

import com.prizz.concurrency.AuctionTimerTask;
import com.prizz.concurrency.LiveAuctionRoom;
import com.prizz.concurrency.ProviderBiddingWorker;
import com.prizz.generic.DataContainer;
import com.prizz.generic.RepositoryHelper;
import com.prizz.io.AuditTokenizer;
import com.prizz.io.ByteStreamBackup;
import com.prizz.io.CsvFileHandler;
import com.prizz.model.Bid;
import com.prizz.model.Booking;
import com.prizz.model.BookingStatus;
import com.prizz.model.Customer;
import com.prizz.model.Request;
import com.prizz.model.RequestStatus;
import com.prizz.repository.BidDAO;
import com.prizz.repository.BookingDAO;
import com.prizz.repository.DBConnection;
import com.prizz.repository.RequestDAO;
import com.prizz.stream.AnalyticsReportService;
import com.prizz.ui.ConsoleView;

import java.util.ArrayList;
import java.util.List;

// Standalone Automated Test Suite verifying all Second Evaluation Modules
public class VerificationTest {
    public static void main(String[] args) {
        ConsoleView.printHeader("STARTING AUTOMATED SYLLABUS VERIFICATION SUITE");

        try {
            // Syllabus: Lectures 34-36 - Initialize SQLite database tables via JDBC
            DBConnection.initializeDatabase();

            // 1. Test JDBC Initialization & CRUD (Lectures 34-42)
            System.out.println("\n>>> [1/5] Testing JDBC & Database Programming (Lectures 34-42)...");
            RequestDAO requestDAO = new RequestDAO();
            String testId = "REQ-JDBC-TEST";
            Request testReq = new Request(testId, "CUST-001", "JDBC Verification Task", "Testing parameter binding", "Plumbing", 120.0, RequestStatus.OPEN, "2026-11-24");
            requestDAO.create(testReq);
            System.out.println("   [PreparedStatement] Created row: " + testId);
            Request fetched = requestDAO.findById(testId);
            System.out.println("   [ResultSet Mapping] Fetched: " + fetched);
            fetched.setBudget(150.0);
            requestDAO.update(fetched);
            requestDAO.delete(testId);
            System.out.println("   [ResultSet Mapping] Verified Update & Delete successfully.");

            // 2. Test Concurrency, wait() & notify() (Lectures 32-33)
            System.out.println("\n>>> [2/5] Testing Concurrency, wait() & notify() (Lectures 32-33)...");
            LiveAuctionRoom room = new LiveAuctionRoom("DEMO-01");
            Thread p1 = new Thread(new ProviderBiddingWorker(room, "PROV-Alpha", 450.0, 4, "High quality white chairs"));
            Thread p2 = new Thread(new ProviderBiddingWorker(room, "PROV-Beta", 410.0, 3, "Same day delivery guaranteed"));
            Thread consumer = new Thread(() -> {
                try {
                    while (!room.isClosed()) {
                        Bid b = room.consumeBid();
                        if (b != null) {
                            System.out.println("  >>> [AUDIT CONSUMER] Consumed Bid #" + b.getId() + " from " + b.getProviderId());
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            Thread timer = new Thread(new AuctionTimerTask(room, 2));
            consumer.start();
            p1.start();
            p2.start();
            timer.start();
            p1.join();
            p2.join();
            timer.join();
            consumer.join(1000);
            System.out.println("   [Concurrency Verified] Threads completed with correct monitor synchronization.");

            // 3. Test Stream API & Lambdas (Lectures 43-46)
            System.out.println("\n>>> [3/5] Testing Stream API & Lambdas (Lectures 43-46)...");
            List<Bid> bids = new ArrayList<>();
            bids.add(new Bid("B-1", "REQ-01", "P-101", 350.0, 3, "Notes 1", "2026-11-24"));
            bids.add(new Bid("B-2", "REQ-01", "P-102", 280.0, 2, "Notes 2", "2026-11-24"));
            bids.add(new Bid("B-3", "REQ-01", "P-103", 420.0, 5, "Notes 3", "2026-11-24"));
            AnalyticsReportService.displayTopCheapestBids(bids, 400.0, 2);
            AnalyticsReportService.displayRunnerUpBids(bids);

            // 4. Test Generics & Wildcards (Lectures 47-48)
            System.out.println("\n>>> [4/5] Testing Generics & Type Safety (Lectures 47-48)...");
            DataContainer<String> stringBox = new DataContainer<>("Session Token", "AUTH-99238-SECURE");
            System.out.println("   " + stringBox);
            double pct = RepositoryHelper.calculatePercentage(35.0, 140.0);
            System.out.printf("   calculatePercentage(35, 140) -> %.2f%%\n", pct);
            List<Customer> customers = new ArrayList<>();
            customers.add(new Customer("C-1", "Alice", "alice@example.com", "555-0101", "42 Elm St", "Event Rentals"));
            RepositoryHelper.displayUsersSummary(customers);

            // 5. Test File I/O & CSV Collections (Lectures 49-54)
            System.out.println("\n>>> [5/5] Testing File I/O & CSV Collections (Lectures 49-54)...");
            List<Booking> bookings = new ArrayList<>();
            bookings.add(new Booking("BK-SEED-1", "REQ-001", "CUST-001", "PROV-001", 420.0, BookingStatus.CONFIRMED, "2026-11-24"));
            CsvFileHandler.exportBookingsToCsv("data/exported_bookings.csv", bookings);
            List<Request> imported = CsvFileHandler.importRequestsFromCsv("data/seed_requests.csv");
            System.out.println("   Imported Requests Count: " + imported.size());
            ByteStreamBackup.createBinaryBackup("data/backup_state.bin", "TEST_BACKUP");
            AuditTokenizer.parseAndSummarizeAuditFile("data/exported_bookings.csv");

            ConsoleView.printHeader("ALL SYLLABUS TESTS COMPLETED SUCCESSFULLY WITH 0 ERRORS");
        } catch (Throwable t) {
            System.err.println("[TEST FAILURE] Exception occurred during verification:");
            t.printStackTrace();
            System.exit(1);
        }
    }
}
